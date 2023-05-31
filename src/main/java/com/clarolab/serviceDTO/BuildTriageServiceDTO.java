/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.BuildTriageMapper;
import com.clarolab.model.*;
import com.clarolab.model.helper.DeduceExplanationHelper;
import com.clarolab.model.helper.PriorityHelper;
import com.clarolab.model.types.StateType;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestTriageService;
import com.clarolab.util.DateUtils;
import com.clarolab.view.ExecutorView;
import com.clarolab.view.GroupedStatView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.clarolab.model.helper.PriorityHelper.convertPriority;

@Component
public class BuildTriageServiceDTO implements BaseServiceDTO<BuildTriage, BuildTriageDTO, BuildTriageMapper> {

    @Autowired
    private BuildTriageService service;

    @Autowired
    private BuildTriageMapper mapper;

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private ReportServiceDTO reportServiceDTO;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    public TTriageService<BuildTriage> getService() {
        return service;
    }

    @Override
    public Mapper<BuildTriage, BuildTriageDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<BuildTriage, BuildTriageDTO, BuildTriageMapper> getServiceDTO() {
        return this;
    }

    public BuildTriageDTO setAssigneeToBuild(UserDTO user, Long buildId) {
        return convertToDTO(service.setAssigneeToBuild(userServiceDTO.findEntity(user), buildId));
    }

    public BuildTriageDTO markBuildAsTriaged(UserDTO user, Long buildId, String note) {
        return convertToDTO(service.markBuildAsTriaged(userServiceDTO.findEntity(user), buildId, note));
    }

    public BuildTriageDTO markBuildAsInvalid(UserDTO user, Long buildId, String note) {
        BuildTriage buildTriage = service.markBuildAsInvalid(userServiceDTO.findEntity(user), buildId, note);
        if (buildTriage == null) {
            return null;
        } else {
            return convertToDTO(buildTriage);
        }
    }

    public BuildTriageDTO markBuildAsDisabled(UserDTO user, Long buildId, String note) {
        return convertToDTO(service.markBuildAsDisabled(userServiceDTO.findEntity(user), buildId, note));
    }

    public BuildTriageDTO markBuildAsEnabled(UserDTO user, Long buildId) {
        return convertToDTO(service.markBuildAsEnabled(userServiceDTO.findEntity(user), buildId));
    }



    public Page<BuildTriageDTO> getPendingBuildTriages(Container container, boolean triaged) {
        List<BuildTriage> list = service.getPendingBuildTriages(container, triaged);
        return new PageImpl<>(convertToDTO(list));
    }

    private LinkedHashSet<UserDTO> findSuggestedAssignee(BuildTriage buildTriage, User user) {
        return service.findSuggestedAssignee(buildTriage, user)
                .stream()
                .map(u -> userServiceDTO.convertToDTO(u))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /* **************************************************************************** */
    /*                             ExecutorView methods                             */
    /* **************************************************************************** */

    private List<ExecutorView> convertToExecutorView(List<BuildTriage> builds) {
        return convertToExecutorView(builds, true);
    }

    private List<ExecutorView> convertToExecutorView(List<BuildTriage> builds, boolean showDisabled) {
        List<ExecutorView> availableViews = new ArrayList<>();
        for (BuildTriage buildTriage : builds) {
            Executor executor = buildTriage.getExecutor();
            if (showDisabled || executor.isHierarchicalyEnabled()) {
                Build build = buildTriage.getBuild();
                availableViews.add(executorServiceDTO.getExecutorViewOfLatestBuild(buildTriage, build, executor, buildTriage.getSpec(), buildTriage.getTriager(), false));
            }

        }
        return availableViews;
    }

    private List<ExecutorView> getAvailableViews(Container container, String search) {
        List<BuildTriage> builds = service.getBuildTriageBy(search, container);
        return convertToExecutorView(builds);
    }

    public List<ExecutorView> getAvailableViewsFor(Container container, boolean assignee, boolean failures, boolean hideDisabled, String search) {
        List<ExecutorView> availableViews = getAvailableViews(container, search);
        return this.filterExecutors(availableViews, assignee, hideDisabled, failures, container);
    }

    private List<ExecutorView> filterExecutors(List<ExecutorView> availableViews, boolean assignee, boolean hideDisabled, boolean failures, Container container) {

        Stream<ExecutorView> stream = availableViews.stream();

        if (hideDisabled)
            stream = stream.filter(ExecutorView::isEnabled);

        if (assignee)
            stream = stream.filter(executorView -> executorView.getAssignee().equals(authContextHelper.getCurrentUserAsDTO()));

        if (failures)
            stream = stream.filter(executorView -> !executorView.isTriaged());

        return stream.collect(Collectors.toList());
    }

    public List<ExecutorView> getTopViews(UserDTO userDTO) {
        User user = userServiceDTO.findEntity(userDTO);
        long tomorrow = DateUtils.beginDay(1);
        List<BuildTriage> todayBuilds = service.findTodaysBuilds(user, tomorrow);

        // Cache version: List<BuildTriage> bigBuilds = buildCache.get(getBuildCacheKey(user, tomorrow), () -> pendingBigBuilds(user, tomorrow));
        // No Cache version:
        List<BuildTriage> bigBuilds = service.pendingBigBuilds(user, tomorrow);

        List<BuildTriage> answer = new ArrayList<>();

        answer.addAll(todayBuilds);
        answer.addAll(bigBuilds);

        return convertToExecutorView(answer, false);
    }

    public LinkedHashSet<UserDTO> suggestedAssignee(Long buildId) {
        BuildTriage build = findEntity(buildId);
        if (build == null) {
            return new LinkedHashSet<>();
        }

        return findSuggestedAssignee(build, authContextHelper.getCurrentUser());
    }

    public String assignPriority(Long buildId, String priority) {
        BuildTriage build = findEntity(buildId);
        if (build == null) {
            return "";
        }

        int newPriority = service.assignPriority(build, PriorityHelper.convertPriority(priority));

        return convertPriority(newPriority);
    }

    public String getTextDetail(Long id) {
        if (id == null) {
            return null;
        }
        BuildTriage buildTriage = findEntity(id);
        if (buildTriage == null) {
            return null;
        }

        Build build = buildTriage.getBuild();

        List<TestTriage> allTestTriage = service.getTextDetail(buildTriage);

        GroupedStatView view = GroupedStatView.builder()
                .executorName(buildTriage.getExecutorName())
                .buildName(String.valueOf(buildTriage.getNumber()))
                .timestamp(buildTriage.getExecutionDate())
                .date(String.valueOf(buildTriage.getDeadline()))
                .passed(testTriageService.countByStateAndTriaged(build, StateType.PASS, true))
                .newFails(allTestTriage.stream().filter(testTriage -> testTriage.isNewFail() && !testTriage.isTriaged()).count())
                .fails(allTestTriage.stream().filter(testTriage -> testTriage.isFail() && !testTriage.isPermanent() && !testTriage.isTriaged()).count())
                .skip(allTestTriage.stream().filter(testTriage -> (testTriage.isSkip() && !testTriage.isPermanent())).count())
                .nowPassing(allTestTriage.stream().filter(testTriage -> testTriage.isNewPass() && !testTriage.isTriaged()).count())
                .triaged(allTestTriage.stream().filter(TestTriage::isTriaged).count())
                .permanent(allTestTriage.stream().filter(TestTriage::isPermanent).count())
                .toTriage(allTestTriage.stream().filter(testTriage -> !testTriage.isTriaged()).count())
                .testsPass(null)
                .testsFail(allTestTriage.stream().filter(testTriage -> testTriage.isFail() && !testTriage.isPermanent() && !testTriage.isTriaged()).map(testTriage -> testTriage.getTestName()).collect(Collectors.toList()))
                .testsSkip(allTestTriage.stream().filter(testTriage -> testTriage.isSkip() && !testTriage.isTriaged()).map(testTriage -> testTriage.getTestName()).collect(Collectors.toList()))
                .testsToTriage(allTestTriage.stream().filter(testTriage -> !testTriage.isTriaged()).map(testTriage -> testTriage.getTestName()).collect(Collectors.toList()))
                .testsTriaged(allTestTriage.stream().filter(testTriage -> testTriage.isTriaged()).map(testTriage -> testTriage.getTestName()).collect(Collectors.toList()))
                .filedAutomations(allTestTriage.stream().filter(testTriage -> testTriage.hasTestBug()).map(testTriage -> testTriage.getTestName()).collect(Collectors.toList()))
                .filedProductBugs(allTestTriage.stream().filter(testTriage -> testTriage.hasProductBug()).map(testTriage -> testTriage.getIssueTicketName()).collect(Collectors.toList()))
                .build();

        view.setTotal(view.getToTriage() + view.getTriaged());
        
        return DeduceExplanationHelper.getPlainDetail(view);

    }
}
