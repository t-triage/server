/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class ReleaseStatusService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private BuildTriageService buildTriageService;

    public boolean getProductStatus(Long id) {
        Product product = productService.find(id);
        if(product==null || !product.isEnabled()) return false;
        List<Container> containers = product.getContainers();
        return containers
                .stream()
                .allMatch(container ->
                        getContainerStatus(container.getId())
                );
    }

    public boolean getContainerStatus(Long id) {
        Container container = containerService.find(id);
        if(container==null || !container.isHierarchicalyEnabled()) return false;

        List<Executor> executors = container.getExecutors();
        return executors
                .stream()
                .allMatch(executor ->
                        getExecutorStatus(executor.getId())
        );
    }

    //it should return true if the executor is ok for deploy
    public boolean getExecutorStatus(Long id) {
        Executor executor = executorService.find(id);
        if (executor == null || !executor.isHierarchicalyEnabled()) return false;

        boolean blockers = executor.getTestCases()
                .stream()
                .anyMatch(this::isThereBlockers);

        return !blockers && isLastBuildReadyToRelease(executor.getLastExecutedBuild());
    }

    private boolean isThereBlockers(TestExecution testExecution) {
        boolean result = false;
        try{
            result = testExecution.getTestCase().getAutomatedTestIssue().isBlocker() || testExecution.getTestCase().getIssueTicket().isBlocker();
        }
        catch (NullPointerException e){
            return false;
        }
        return result;
    }

    private boolean isThereBlockers(TestTriage testTriage) {
        boolean result = isThereBlockers(testTriage.getTestExecution());
        result = result && (testTriage.isTriaged() || testTriage.isPassed());
        return result;
    }

    private boolean isLastBuildReadyToRelease(Build lastExecutedBuild) {
        //for now... the single rules AllTriaged -> OK

        BuildTriage buildTriage = buildTriageService.find(lastExecutedBuild);
        return buildTriage.isTriaged() || buildTriage.isAutomatedTriaged();
    }

    //it should return true if the pipeline is ok for deploy
    public boolean getPipelineStatus(Long id) {
        Pipeline entity = pipelineService.find(id);
        if (entity == null || !entity.isEnabled()) return false;

        List<TestTriage> testTriages = pipelineService.ongoingTestTriages(entity);

        boolean answer = testTriages.stream().anyMatch(this::isThereBlockers);

        return answer;
    }

    public String getPipelineHelp(Long id) {
        Pipeline entity = pipelineService.find(id);
        if (entity == null || !entity.isEnabled()) return "Specify a Pipeline ID";

        StringBuffer answer = new StringBuffer();

        answer.append("Sample");
        answer.append("\n");
        answer.append("curl -X GET -H ");

        return answer.toString();
    }
}
