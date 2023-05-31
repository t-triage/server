/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.QAReportApplication;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.DeducedReasonType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ErrorDetailService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static com.clarolab.model.types.DeducedReasonType.Rule45;

@Getter
public abstract class AbstractRule implements Rule {

    @Autowired
    ErrorDetailService errorDetailService;

    public TestTriage.TestTriageBuilder processFirstTriage(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {
        if (testExecution.isPassed()) {
            processPass(testTriageBuilder);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule30);
        } else {
            processFail(testTriageBuilder);
            testTriageBuilder.rank(4);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule31);
            processError(StatusType.FAIL, testExecution);
        }

        return testTriageBuilder;
    }

    public TestTriage.TestTriageBuilder processNoUnique(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {
        if (testExecution.isPassed()) {
            processPass(testTriageBuilder);
            testTriageBuilder.rank(0);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule36);
        } else {
            processFail(testTriageBuilder);
            testTriageBuilder.rank(4);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule37);
            processError(StatusType.FAIL, testExecution);
        }

        return testTriageBuilder;
    }

    public TestTriage.TestTriageBuilder processDisabledEngine(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {
        if (testExecution.isPassed()) {
            processPass(testTriageBuilder);
            testTriageBuilder.rank(0);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule38);
        } else {
            processFail(testTriageBuilder);
            testTriageBuilder.rank(4);
            testTriageBuilder.stateReasonType(DeducedReasonType.Rule39);
            processError(StatusType.FAIL, testExecution);
        }

        return testTriageBuilder;
    }

    protected void processError(StatusType statusType, TestExecution testExecution) {
        if (errorDetailService == null) {
            ApplicationContext applicationContext = QAReportApplication.getApplicationContext();
            if (applicationContext != null) {
                errorDetailService = applicationContext.getBean(ErrorDetailService.class);
            }
        }

        if (errorDetailService != null) {
            errorDetailService.processErrorStack(statusType, testExecution);
        }
    }
    
    protected boolean hasPassedInVersion(TestTriage previousTestTriage, TestExecution testExecution) {
        return !previousTestTriage.getProduct().isHasMultipleEnvironment() && previousTestTriage.hasSameProductVersion(testExecution.getProductVersion()) && (previousTestTriage.getStateReasonType() == Rule45 || previousTestTriage.isPassed());
    }



}
