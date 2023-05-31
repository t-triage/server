/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteStatusResultType;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Log
public class RobotReport {

    private ApplicationContextService context;
    private String generated;
    private String generator;
    private SuiteReportMessage errors;
    private StatisticsReport statistics;
    private List<SuiteReport> suite;

    public StatusType getStatus(){
        return this.convertStatus(this.getMainSuite().getExecutionStatus());
    }

    public int getPassed(){
        return statistics.getPassed(this.getMainSuitesId());
    }

    public int getFailed(){

        return statistics.getFailed(this.getMainSuitesId());
    }

    public int getSkipped(){
        return 0;
    }

    public Long getGeneratedDate(){
        return DateUtils.convertDate(generated);
    }

    public Long getDuration(){
        try {
            return this.getMainSuite().getDuration();
        }catch(Exception e){
            return Long.parseLong(this.getMainSuite().getStatus().getElapsedtime());
        }

//        if(getStatus().equals(StatusType.PASS)){
//            return this.getMainSuite().getDuration();
//        }
//        if(getStatus().equals(StatusType.FAIL)){
//            try {
//                return Long.parseLong(this.getMainSuite().getStatus().getElapsedtime());
//            }catch(NumberFormatException e){
//                return this.getMainSuite().getDuration();
//            }
//        }
//        return 0L;
    }


    public List<TestExecution> getTests(){
        return this.getTests(this.getMainSuite());
    }

    private List<TestExecution> getTests(SuiteReport suite){
        //First suite contains the main execution
        //Second suite is Retry-1
        //Third suite is Retry-2 and so ...
        //Main and Retry suites also can contain suites on them.
        List<TestExecution> tests = Lists.newArrayList();
        if(suite != null && suite.isTestCasesPresent()) {
            suite.setContext(context);
            tests.addAll(suite.getTestCases());
        }else if(!CollectionUtils.isEmpty(suite.getSuite()))
            suite.getSuite().forEach( s -> tests.addAll(this.getTests(s)));

        return tests;
    }

    private SuiteReport getMainSuite(){
        return suite.stream().findFirst().orElse(null);
    }

    private List<String> getMainSuitesId(){
        return this.getMainSuite().getSuite().stream().map(s -> s.getId()).collect(Collectors.toList());
    }

    private StatusType convertStatus(SuiteStatusResultType status) {
        switch (status) {
            case FAIL:
                return StatusType.FAIL;
            case PASS:
                return StatusType.PASS;
            default:
                return StatusType.UNKNOWN;
        }
    }

}
