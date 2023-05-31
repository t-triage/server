package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class MainProtractorV2 {

    private ApplicationContextService context;
    private ProtractorTestCaseV2 testCase;
    private Map<String, String> screenshots;

    public StatusType getStatus(){
        return testCase.getStatus();
    }

    public int getPassed(){
        return testCase.getStatus().equals(StatusType.PASS) ? 1 : 0;
    }

    public int getFailed(){
        return testCase.getStatus().equals(StatusType.FAIL) ? 1 : 0;
    }

    public int getSkipped(){
        return testCase.getStatus().equals(StatusType.SKIP) ? 1 : 0;
    }

    public long getDuration(){
        return testCase.getDuration();
    }

    public TestExecution getTestCase(){
        return getTestCase(false);
    }

    public TestExecution getTestCase(boolean isForDebug){
        testCase.setContext(context);
        return testCase.getTestCase(isForDebug);
    }
}
