/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.controller.impl.PageableHelper;
import com.clarolab.dto.*;
import com.clarolab.dto.db.TestTriagePassedDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ExecutorMapper;
import com.clarolab.model.*;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StateType;
import com.clarolab.service.*;
import com.clarolab.view.ExecutorView;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;

@Component
@Log
public class ExecutorServiceDTO implements BaseServiceDTO<Executor, ExecutorDTO, ExecutorMapper> {

    @Autowired
    private ExecutorService service;

    @Autowired
    private ExecutorMapper mapper;

    @Override
    public TTriageService<Executor> getService() {
        return service;
    }

    @Override
    public Mapper<Executor, ExecutorDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Executor, ExecutorDTO, ExecutorMapper> getServiceDTO() {
        return this;
    }

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageServiceDTO testTriageServiceDTO;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Autowired
    private BuildTriageServiceDTO buildTriageServiceDTO;

    @Autowired
    private ReportServiceDTO reportServiceDTO;
    
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ExecutorImportService executorImportService;

    @Autowired
    private ExecutorImportCSVService executorImportCSVService;

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    /* **************************************************************************** */
    /*                             ExecutorView methods                             */
    /* **************************************************************************** */

    public Page<ExecutorView> list(Pageable pageable, FilterDTO filterDTO) {
        //Instead a ContainerDTO I am using a Container in order to avoid DTO->Entity->DTO conversion
        Long containerId = filterDTO.getContainerId();
        Container container = containerId == null || containerId > 0 ? containerService.find(containerId) : null;
        List<ExecutorView> list = buildTriageServiceDTO.getAvailableViewsFor(container, filterDTO.getAssignee(), filterDTO.getFailures(), filterDTO.getHideDisabled(), filterDTO.getSearch());
        sortViews(pageable, list);
        return PageableHelper.getPageable(pageable, list);
    }


    public ExecutorView getExecutorViewOfLatestBuild(Executor executor, boolean includeTests, boolean includeDisable) {
        Build build;
        BuildTriage buildTriage;
        try {
            buildTriage = buildTriageService.getLastTriageOf(executor);

            if (buildTriage == null) return null;

            if (!includeDisable && !buildTriage.isEnabled()) return null;

            return getExecutorViewOfLatestBuildTriage(buildTriage, buildTriage.getBuild(), executor, buildTriage.getSpec(), buildTriage.getTriager(), includeTests);
        } catch (EntityNotFoundException e) {
            log.warning("Executor was not found");
            return null;
        }
    }

    public List<TestTriagePassedDTO> getExecutorViewOfLatestBuildAndTestPass(Executor executor, boolean includeTests, boolean includeDisable) {
        Build build;
        BuildTriage buildTriage;
        try {
            buildTriage = buildTriageService.getLastTriageOf(executor);

            if (buildTriage == null) return null;

            if (!includeDisable && !buildTriage.isEnabled()) return null;

            return getExecutorViewOfLastestBuildAndTestPass(buildTriage, buildTriage.getBuild(), executor, buildTriage.getSpec(), buildTriage.getTriager(), includeTests);
        } catch (EntityNotFoundException e) {
            log.warning("Executor was not found");
            return null;
        }
    }


    public ExecutorView getExecutorViewOfLatestBuild(BuildTriage buildTriage, Build build, Executor executor, TriageSpec spec, User triager, boolean includeTests) {
        return getExecutorViewOfLatestBuildTriage(buildTriage, build, executor, spec, triager, includeTests);
    }

    public List<TestTriagePassedDTO> getExecutorViewOfLastestBuildAndTestPass (BuildTriage buildTriage, Build lastExecutedBuild, Executor executor, TriageSpec triageSpec, User triagger, boolean includeTests) {
        Map<String, List<TestTriagePassedDTO>> triageMap = Maps.newHashMap();

        if (includeTests)
            triageMap = getCategorizedFilteredAndSortedTestTriagesPass(lastExecutedBuild);

        return triageMap.get("PASS");
    }

    public ExecutorView getExecutorViewOfLatestBuildTriage(BuildTriage buildTriage, Build lastExecutedBuild, Executor executor, TriageSpec triageSpec, User triagger, boolean includeTests) {

        final long total = testTriageService.countBy(lastExecutedBuild);
        final long newFails = testTriageService.countByStateAndTriaged(lastExecutedBuild, StateType.NEWFAIL, false);
        final long fails = testTriageService.countByStateAndTriaged(lastExecutedBuild, StateType.FAIL, false) + testTriageService.countByStateAndTriaged(lastExecutedBuild, StateType.PERMANENT, false);
        final long newPass = testTriageService.countByStateAndTriaged(lastExecutedBuild, StateType.NEWPASS, true);
        final long triageDone = testTriageService.countByStateNotAndTriaged(lastExecutedBuild, StateType.PASS, true);

        final long toTriage = testTriageService.countByStateNotAndTriaged(lastExecutedBuild, StateType.PASS, false);
        final long autoTriaged = testTriageService.countByStateNotAndTag(lastExecutedBuild, StateType.PASS, AUTO_TRIAGED);

        Map<String, List<TestTriageDTO>> triageMap = Maps.newHashMap();

        if (includeTests)
            triageMap = getCategorizedFilteredAndSortedTestTriages(lastExecutedBuild);

        return ExecutorView.builder()
                .buildTriage(buildTriage)
                .allTestTriages(triageMap)
                .executor(executor)
                .triagger(userServiceDTO.convertToDTO(triagger))
                .triageSpec(triageSpec)

                /*All totals now are pre calculated */
                .toTriage(toTriage)
                .autoTriaged(autoTriaged)
                .totalNewFails(newFails)
                .totalFails(fails)
                .totalNewPass(newPass)
                .totalTriageDone(triageDone)
                .totalTests(total)

                .build();
    }

    public Map<String, List<TestTriageDTO>> getCategorizedFilteredAndSortedTestTriages(Build lastExecutedBuild) {

        Map<String, List<TestTriageDTO>> triageMap;

        final List<TestTriage> allTestTriage = testTriageService.findAllByBuildAndStateNot(lastExecutedBuild, StateType.PASS);

        triageMap = getCategorizedFilteredAndSortedTestTriages(allTestTriage);

        return triageMap;

    }

    public Map<String, List<TestTriagePassedDTO>> getCategorizedFilteredAndSortedTestTriagesPass(Build lastExecutedBuild) {

        Map<String, List<TestTriagePassedDTO>> triageMap;

        final List<TestTriage> allTestTriage = testTriageService.findAllByBuildAndState(lastExecutedBuild, StateType.PASS);

        triageMap = getPassedTestTriages(allTestTriage);

        return triageMap;

    }

    public Map<String, List<TestTriagePassedDTO>> getPassedTestTriages(List<TestTriage> allTestTriage ) {

        final Map<String, List<TestTriagePassedDTO>> triageMap = Maps.newHashMap();

        Stream<TestTriage> passStream = allTestTriage.stream().filter(testTriage -> testTriage.isPass() && !testTriage.isExpired());

        triageMap.put("PASS", passStream.map(testTriage -> new TestTriagePassedDTO(testTriage.getId(), testTriage.getTimestamp(), testTriage.getTestExecution(), testTriage.getTriager())).collect(Collectors.toList()));

        return triageMap;

    }

    public Map<String, List<TestTriageDTO>> getCategorizedFilteredAndSortedTestTriagesForPipelines(List<TestTriage> allTestTriage ) {

        final Map<String, List<TestTriageDTO>> triageMap = Maps.newHashMap();

        Stream<TestTriage> newFailStream = allTestTriage.stream().filter(testTriage -> testTriage.isNewFail() && !testTriage.isTriaged());
        Stream<TestTriage> failStream = allTestTriage.stream().filter(testTriage -> ((testTriage.isSkip() && testTriage.isFirtsTriage()) || testTriage.isFail() || testTriage.isPermanent()) && !testTriage.isTriaged());
        Stream<TestTriage> notExecutedStream = allTestTriage.stream().filter(testTriage -> testTriage.isNotExecuted() && !testTriage.isTriaged());
        Stream<TestTriage> triageStream = allTestTriage.stream().filter(testTriage -> testTriage.isTriaged() && !(testTriage.isNewPass() && !testTriage.isUpdatedByUser()));

        triageMap.put("NOTEXECUTED", notExecutedStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("FAIL", failStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("NEWFAIL", newFailStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("TRIAGEDONE", triageStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));

        return triageMap;

    }

    public Map<String, List<TestTriageDTO>> getCategorizedFilteredAndSortedTestTriages(List<TestTriage> allTestTriage ) {

        final Map<String, List<TestTriageDTO>> triageMap = Maps.newHashMap();

        Stream<TestTriage> newFailStream = allTestTriage.stream().filter(testTriage -> testTriage.isNewFail() && !testTriage.isTriaged());
        Stream<TestTriage> failStream = allTestTriage.stream().filter(testTriage -> ((testTriage.isSkip() && testTriage.isFirtsTriage()) || testTriage.isFail() || testTriage.isPermanent()) && !testTriage.isTriaged());
        Stream<TestTriage> nowPassingStream = allTestTriage.stream().filter(testTriage -> testTriage.isNewPass() && !testTriage.isUpdatedByUser());
        Stream<TestTriage> triageStream = allTestTriage.stream().filter(testTriage -> testTriage.isTriaged() && !(testTriage.isNewPass() && !testTriage.isUpdatedByUser()));

        triageMap.put("NOWPASSING", nowPassingStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("FAIL", failStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("NEWFAIL", newFailStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));
        triageMap.put("TRIAGEDONE", triageStream.map(testTriage -> testTriageServiceDTO.convertToKanban(testTriage)).collect(Collectors.toList()));

        return triageMap;

    }

    public List<ExecutorView> getExecutorViewsBetween(long timestampFrom, long timestampTo) {
        List<Executor> executors = service.findAllByTimestampBetween(timestampFrom, timestampTo);
        return getExecutorListFrom(executors);
    }

    public List<ExecutorView> getExecutorListFrom(List<Executor> executors) {
        List<ExecutorView> list = Lists.newArrayList();
        executors.forEach(executor -> {
            ExecutorView executorView = getExecutorViewOfLatestBuild(executor, false, false);
            if (executorView != null) list.add(executorView);
        });
        return list;
    }

    private void sortViews(Pageable pageable, List<ExecutorView> list) {

        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) return;

        sort.forEach(order -> {
            //TODO Fede, sacar en el futuro a un HashMap con todos los sort criteria
            if (order.getProperty().equalsIgnoreCase("shortpriority"))
                list.sort(Comparator.comparing(ExecutorView::getShortPriority));
            else if (order.getProperty().equalsIgnoreCase("executorname"))
                list.sort(Comparator.comparing(ExecutorView::getExecutorName, String.CASE_INSENSITIVE_ORDER));

            if (order.isAscending())
                Collections.reverse(list);
        });
    }


    public Executor getExecutorById(long parseLong) {
        return service.getExecutorById(parseLong);
    }

    public boolean populate(long id) {
        propertyService.warmUp();
        try {
            service.populateRequest(buildTriageService.find(id).getExecutor());
        } catch (ExecutorServiceException e) {
            log.log(Level.SEVERE, e.getMessage());
            return false;
        }
        return true;
    }

    public List<String> search(String name) {
        return service.search(name);
    }

    public List<KeyValuePair> getExecutorNames(Long id) {
        return service.getExecutorNames(id);
    }

    public List<ExecutorView> getExecutorViews(Long containerId, boolean enabled) {
        List<Executor> executors;
        if (containerId == null) {
            executors = service.findAllByEnabled(enabled);
        } else {
            Container container = containerService.find(containerId);
            executors = service.findAllByContainerAndEnabled(container, enabled);
        }

        return getExecutorListFrom(executors);
    }

    public List<ReportDTO> history(Long executorId) {
        return  reportServiceDTO.convertToDTONoExecutions(service.getBuildHistory(executorId));
    }
    
    public String upload(Executor executor, String xml, ReportType reportType) {
        return service.upload(executor, convertXMLToJson(xml), reportType);
    }

    public String importReport(String file) {
        String result = "";
        try {
            result = executorImportService.importReport(file);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Couldn't import executor CSV file", e);
            result = result + e.getLocalizedMessage();
        }
        return result;
    }

    public String importCSVReport(String CSVContent, String separator, String format) {
        return executorImportCSVService.importReport(CSVContent, separator, format);
    }

    public String convertXMLToJson(String json) {
        if (json.startsWith("<?xml")) {
            json = json.substring(json.indexOf(">")+1);
            json = json.replaceAll("\\\\\"", "\"");
            json = json.replaceAll("\\\n", "");
            json = XML.toJSONObject(json).toString();
        }

        return json;
    }

    public List<ExecutorStatChartDTO> getGrowthStats(Long executorid, Long from, Long to) {
        return service.getGrowthStats(executorid, from, to);
    }

    public List<ExecutorStatChartDTO> getCommitsStats(Long executorid, Long from, Long to) {
        return service.getCommitsStats(executorid, from, to);
    }

    public List<ExecutorStatChartDTO> getPassingStats(Long executorid, Long from, Long to) {
        return service.getPassingStats(executorid, from, to);
    }

    public List<ExecutorStatChartDTO> getStabilityStats(Long executorid, Long from, Long to) {
        return service.getStabilityStats(executorid, from, to);
    }

    public List<ExecutorStatChartDTO> getTriageDoneStats(Long executorid, Long from, Long to) {
        return service.getTriageDoneStats(executorid, from, to);
    }

    public List<ExecutorDTO> getExecutorEnabled() {
        List<Executor> executors = service.getExecutorEnabled();
        return executorServiceDTO.convertToDTO(executors);
    }

}
