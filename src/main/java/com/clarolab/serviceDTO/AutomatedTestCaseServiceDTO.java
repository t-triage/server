/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.impl.OptimizedPage;
import com.clarolab.dto.AutomatedTestCaseDTO;
import com.clarolab.dto.FilterDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.AutomatedTestCaseMapper;
import com.clarolab.model.TestCase;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.StateType;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestCaseService;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clarolab.util.SearchSpecificationUtil.getSearchSpec;

@Component
public class AutomatedTestCaseServiceDTO implements BaseServiceDTO<TestCase, AutomatedTestCaseDTO, AutomatedTestCaseMapper> {

    @Autowired
    private TestCaseService service;

    @Autowired
    private AutomatedTestCaseMapper mapper;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    public TTriageService<TestCase> getService() {
        return service;
    }

    @Override
    public Mapper<TestCase, AutomatedTestCaseDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TestCase, AutomatedTestCaseDTO, AutomatedTestCaseMapper> getServiceDTO() {
        return this;
    }

    public Page<AutomatedTestCaseDTO> filterList(String[] criteria, Pageable pageable, FilterDTO filters) {
        Long lastExecution = getLastExecutionLong(filters);
        List<StateType> stateFilter = getCurrentStatusStateType(filters);
        String tags = getTags(filters);
        List<Long> components = getComponentsFilterList(filters);
        OptimizedPage<TestCase> list = service.findAllFilteredByAutomatedTest(getSearchSpec(criteria), lastExecution, filters.getName(), filters.getExecutorName(), stateFilter, filters.getHideNoSuite(), pageable, tags,  filters.getAutomatedAssignee(), filters.getPipeline(), components);
        Page answer = OptimizedPage.newContent(list, convertToDTO(list.getContent()));

        return answer;
    }


    private Long getLastExecutionLong(FilterDTO filters) {
        Long lastExecutionLong = null;
        String lastExecution = filters.getLastExecution();
        if (lastExecution != null) {
            switch (lastExecution) {
                case "yesterday":
                    lastExecutionLong = DateUtils.offSetDays(-1);
                    break;
                case "last-3-days":
                    lastExecutionLong = DateUtils.offSetDays(-3);
                    break;
                case "last-week":
                    lastExecutionLong = DateUtils.offSetDays(-7);
                    break;
                case "last-2-weeks":
                    lastExecutionLong = DateUtils.offSetDays(-14);
                    break;
                case "last-month":
                    lastExecutionLong = DateUtils.offSetDays(-30);
                    break;
            }
        }
        return lastExecutionLong;
    }

    private List<StateType> getCurrentStatusStateType(FilterDTO filters) {
        List<StateType> currentStatusStateType = null;
        String currentStatus = filters.getCurrentState();
        if (currentStatus != null || currentStatus != "all") {
            switch (currentStatus) {
                case "fail":
                    currentStatusStateType = Arrays.asList(StateType.NEWFAIL, StateType.FAIL, StateType.PERMANENT);
                    break;
                case "pass":
                    currentStatusStateType = Arrays.asList(StateType.PASS, StateType.NEWPASS);
                    break;
                case "skipped":
                    currentStatusStateType = Arrays.asList(StateType.SKIP);
                break;
            }
        }
        return currentStatusStateType;
    }
    private String getTags(FilterDTO filters) {
        String tags = null;

        if ( filters.getFlakyTest() != null) {
            String f = filters.getFlakyTest();
            switch (f) {
                case "flaky":
                    tags = TagHelper.FLAKY_TRIAGE;
                    break;
                case "solid":
                    tags = TagHelper.SOLID_TEST;
                    break;
                 default:
                    tags = null;
                    break;
            }
        }
        return tags;
    }
    private boolean isFilter(FilterDTO filters, String match) {
        if (filters == null) {
            return false;
        }
        String flakyTest = filters.getFlakyTest();
        if (StringUtils.isEmpty(flakyTest)) {
            return false;
        }
        return match.equalsIgnoreCase(flakyTest);
    }

    private List<Long> getComponentsFilterList (FilterDTO filters) {
        List<Long> componentList = new ArrayList<>();

        componentList.add(filters.getComponent1());
        componentList.add(filters.getComponent2());
        componentList.add(filters.getComponent3());
        componentList.add(filters.getComponent4());
        componentList.add(filters.getComponent5());
        componentList.add(filters.getComponent6());

        return componentList;
    }
}
