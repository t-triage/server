/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.processors;

import com.clarolab.agents.errors.ErrorProcessor;
import com.clarolab.agents.errors.UndefinedErrorProcessor;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.detail.ErrorDetail;
import com.clarolab.model.helper.exparser.StackTrace;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ErrorDetailService;
import com.clarolab.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.AUTOTRIAGE_SAME_ERROR_TEST;
import static com.clarolab.util.Constants.DEFAULT_AUTOTRIAGE_SAME_ERROR_TEST_ENABLED;

public abstract class AbstractStateProcessor {

    private Map<StatusType, ErrorProcessor> errorProcessorMap;

    @Autowired
    private ErrorDetailService errorDetailService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    public void setErrorProcessorMap(List<ErrorProcessor> errorProcessorMap) {
        this.errorProcessorMap = errorProcessorMap.stream().collect(Collectors.toMap(ErrorProcessor::errorProcessorType, Function.identity()));
    }

    public ErrorProcessor getErrorProcessor(StatusType type) {
        return errorProcessorMap.getOrDefault(type, new UndefinedErrorProcessor());
    }

    protected Optional<TestTriage> getLastTestTriaged(List<TestTriage> previousTests) {
        //the previousTests list ir ordered by id DESC.
        if (previousTests.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(previousTests.get(0));
        }
    }

    protected ErrorType processErrorStack(StatusType type, TestExecution testExecution, TestTriage latestTestTriaged) {

        //Moved to the end in order to save all errors
        /*boolean autoTriage = propertyService.valueOf(AUTOTRIAGE_SAME_ERROR_TEST, DEFAULT_AUTOTRIAGE_SAME_ERROR_TEST_ENABLED);
        if (!autoTriage) return ErrorType.UNDEFINED;*/

        if (testExecution == null) return ErrorType.NOT_EQUAL;

        ErrorProcessor errorProcessor = getErrorProcessor(type);
        List<StackTrace> stackTracesExecution = errorProcessor.analyzeStackTrace(testExecution.getErrorStackTrace());
        stackTracesExecution.forEach(stack -> {

                    ErrorDetail errorDetail = ErrorDetail
                            .builder()
                            .testExecution(testExecution)
                            .previousTestTriage(latestTestTriaged)
                            .exceptionType(stack.getExceptionType())
                            .message(stack.getMessage())
                            .causedBy(stack.getCausedBy() == null ? "" : stack.getCausedBy().toString())
                            .build();

                    errorDetailService.save(errorDetail);
                }
        );

        boolean autoTriage = propertyService.valueOf(AUTOTRIAGE_SAME_ERROR_TEST, DEFAULT_AUTOTRIAGE_SAME_ERROR_TEST_ENABLED);
        if (!autoTriage) return ErrorType.UNDEFINED;

        if (latestTestTriaged == null || stackTracesExecution.isEmpty()) return ErrorType.NOT_EQUAL;

        List<StackTrace> stackTracesTriages = errorProcessor.analyzeStackTrace(latestTestTriaged.getTextExecutionStackTrace());
        if (stackTracesTriages.isEmpty()) return ErrorType.NOT_EQUAL;

        return errorProcessor.process(stackTracesExecution.get(0), stackTracesTriages.get(0));

    }
}
