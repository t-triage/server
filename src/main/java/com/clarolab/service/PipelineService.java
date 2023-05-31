/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.model.*;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.PipelineRepository;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class PipelineService extends BaseService<Pipeline> {

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private PipelineTestService pipelineTestService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Override
    public BaseRepository<Pipeline> getRepository() {
        return pipelineRepository;
    }

    public List<Pipeline> search(String search) {
        if (search == null || search.length() < MIN_SEARCH_LENGHT_) {
            return Lists.newArrayList();
        }

        search = StringUtils.prepareStringForSearch(search);
        List<Pipeline> list = pipelineRepository.findAllByNameIgnoreCaseLike(search);

        return list;
    }

    public void assignToPipeline(Pipeline entity, List<Long> testCaseIds) {
        for (Long testCaseID : testCaseIds) {
            TestCase testCase = testCaseService.find(testCaseID);
            PipelineTest aCase = pipelineTestService.find(entity, testCase);
            if (aCase == null) {
                PipelineTest relation = new PipelineTest();
                relation.setTest(testCase);
                relation.setPipeline(entity);
                pipelineTestService.save(relation);
            } else {
                // Nothing to do, pipeline and test were already related
            }
        }
    }

    public List<PipelineTest> findAll(Pipeline pipeline) {
        return pipelineTestService.findAll(pipeline);
    }

    public TriageSpec createOrGetSpec(Pipeline pipeline) {
        TriageSpec triageSpec = triageSpecService.getTriageSpec(pipeline);
        if (triageSpec == null) {

            triageSpec = new TriageSpec();
            triageSpec.setPipeline(pipeline);
            triageSpec.setExecutor(null);

            Container container = containerService.findFirst();
            TriageSpec containerTriageSpec = triageSpecService.getTriageSpec(container);
            triageSpec.setContainer(container);
            triageSpec.setTriager(containerTriageSpec.getTriager());
            triageSpec.setPriority(containerTriageSpec.getPriority());
            triageSpec.setEveryWeeks(containerTriageSpec.getEveryWeeks());
            triageSpec.setExpectedMinAmountOfTests(containerTriageSpec.getExpectedMinAmountOfTests());
            triageSpec.setExpectedPassRate(containerTriageSpec.getExpectedPassRate());
            triageSpec.setFrequencyCron(containerTriageSpec.getFrequencyCron());
            triageSpec.setLastCalculatedDeadline(containerTriageSpec.getLastCalculatedDeadline());

            triageSpec = triageSpecService.save(triageSpec);
        }

        return triageSpec;
    }

    public List<TestTriage> ongoingTestTriages(Pipeline pipeline) {
        List<PipelineTest> relations = findAll(pipeline);
        List<TestTriage> answer = new ArrayList<>(relations.size());
        TriageSpec spec = createOrGetSpec(pipeline);

        for (PipelineTest test : relations) {
            List<TestTriage> triages = testTriageService.findAllOngoingTests(test.getTest());
            if (triages.isEmpty()) {
                TestTriage noTriage = new TestTriage();
                TestExecution noRunTest = new TestExecution();
                noRunTest.setTestCase(test.getTest());
                noRunTest.setStatus(StatusType.NO_TESTS);
                noTriage.setTestExecution(noRunTest);
                noTriage.setCurrentState(StateType.NOT_EXECUTED);
                noTriage.setTriaged(false);
                noTriage.setTriager(spec.getTriager());
                answer.add(noTriage);
            } else {
                for (TestTriage entity : triages) {
                    answer.add(entity);
                }
            }
        }

        return answer;
    }

    public List<Container> containers() {
        List<Container> containers = new ArrayList<>();
        List<Pipeline> pipelines = pipelineRepository.findAllByEnabledIsTrue();
        for (Pipeline p : pipelines) {
            TriageSpec ts = triageSpecService.getTriageSpec(p);
            containers.add(ts.getContainer());
        }
        return containers;
    }

    public List<Pipeline> getPipelinesEnabled() {
        return pipelineRepository.findAllByEnabledIsTrue();
    }

    public List<Pipeline> findPipelinesByContainer(Long containerId) {
        Container container = containerId != null ? containerService.find(containerId) : null;
        return triageSpecService.findPipelinesByContainer(container);
    }
}