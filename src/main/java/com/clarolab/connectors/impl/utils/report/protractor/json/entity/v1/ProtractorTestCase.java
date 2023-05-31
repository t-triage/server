package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProtractorTestCase extends AbstractTestCreator {

    private String classname;
    private String name;
    private String time;
    private ProtractorTestCaseFailure failure;

    public TestExecution getTest(){
        return getTest(false);
    }

    public TestExecution getTest(boolean isForDebug){
        return TestExecution.builder()
                .testCase(getTestCase(name, classname, isForDebug))
                .duration(getTime(time))
                .status(getStatus())
                .errorDetails(failure.getError())
                .errorStackTrace(failure.getDetailedError())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

    public StatusType getStatus(){
        if(failure != null && !failure.noErrorPresent())
            return StatusType.FAIL;
        //TODO: put here a Skip case
        return StatusType.PASS;
    }
}
