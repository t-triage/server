package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1;

import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class MainProtractor {

    @Builder.Default
    private List<ProtractorSuite> suites = Lists.newArrayList();

    public void addSuite(ProtractorSuite suite){
        suites.add(suite);
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
        return getTests(false);
    }

    public List<TestExecution> getTests(boolean isForDebug){
        List<TestExecution> tests = Lists.newArrayList();
        suites.forEach(suite -> tests.addAll(suite.getTestCases(isForDebug)));
        return tests;
    }
}
