/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Log
public class CucumberSuite extends CucumberBase {

    private String uri;
    private List<CucumberTestCase> testCases;

    @Builder
    private CucumberSuite(String id, String name, String description, String uri, List<String> tags, List<CucumberTestCase> testCases){
        super(id, name, description, tags);
        this.uri = uri;
        this.testCases = testCases;
    }

    public int getPassed(){
        return (int) testCases.stream().filter(test -> test.getStatus().equals(StatusType.PASS)).count();
    }

    public int getFailed(){
        return (int) testCases.stream().filter(test -> test.getStatus().equals(StatusType.FAIL)).count();
    }

    public int getSkipped(){
        return (int)testCases.stream().filter(test -> test.getStatus().equals(StatusType.SKIP)).count();
    }

    public StatusType getStatus(){
        if(this.getFailed() > 0)
            return StatusType.FAIL;
        if( this.getSkipped() > 0)
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    public List<TestExecution> getTestCases(){
        List<TestExecution> tests = Lists.newArrayList();
        testCases.forEach(test -> test.setContext(context));
        this.testCases.forEach(test -> {
            TestExecution testCase = test.getTest(this);
            testCase.setName(test.isBackground() ? String.format(test.getName(), this.getName()) : test.getName());
            testCase.setSuiteName(this.getId());
            testCase.setLocationPath(this.getUri());
            testCase.add(test.getSteps());
            tests.add(testCase);
        });
        return tests;
    }

    public long getDuration(){
        return testCases.stream().mapToLong(test -> test.getDuration()).sum();
    }

}
