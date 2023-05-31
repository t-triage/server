/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Log
@Builder
public class MainCucumber {

    @Builder.Default
    private List<CucumberSuite> suites = Lists.newArrayList();
    private ApplicationContextService contextService;

    public void addSuite(CucumberSuite suite){
        this.suites.add(suite);
    }

    public StatusType getStatus(){
        if(suites.stream().filter(suite -> suite.getStatus().equals(StatusType.FAIL)).count() > 0)
            return StatusType.FAIL;
        if(suites.stream().filter(suite -> suite.getStatus().equals(StatusType.SKIP)).count() > 0)
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    public int getPassed(){
        return suites.stream().mapToInt(suite -> suite.getPassed()).sum();
    }

    public int getFailed(){
        return suites.stream().mapToInt(suite -> suite.getFailed()).sum();
    }

    public int getSkipped(){
        return suites.stream().mapToInt(suite -> suite.getSkipped()).sum();
    }

    public long getDuration(){
        return suites.stream().mapToLong(suite -> suite.getDuration()).sum();
    }

    public List<TestExecution> getTests(){
        for(CucumberSuite s: suites){
            if(s.getContext() == null)
                s.setContext(contextService);
        }

        List<TestExecution> tests = Lists.newArrayList();
        suites.forEach(suite -> tests.addAll(suite.getTestCases()));
        return tests;
    }
}
