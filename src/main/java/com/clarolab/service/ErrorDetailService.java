/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.agents.errors.ErrorProcessor;
import com.clarolab.agents.errors.FailErrorProcessor;
import com.clarolab.agents.errors.UndefinedErrorProcessor;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.detail.ErrorDetail;
import com.clarolab.model.helper.exparser.StackTrace;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StatusType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ErrorDetailRepository;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.ConcurrentHashMultiset;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.*;

@Service
@Log
public class ErrorDetailService extends BaseService<ErrorDetail> {

    private Map<StatusType, ErrorProcessor> errorProcessorMap;

    @Autowired
    private ErrorDetailRepository errorDetailRepository;

    @Autowired
    private ErrorDetailService errorDetailService;
    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private PropertyService propertyService;


    @Override
    public BaseRepository<ErrorDetail> getRepository() {
        return errorDetailRepository;
    }

    public List<KeyValuePair> getAllGroupedBy(){
        int limit = propertyService.valueOf(Constants.STATS_FAIL_EXCEPTION_TOP_LIMIT, DEFAULT_STATS_FAIL_EXECPTION_TOP_LIMIT);
        List<Object[]> list = errorDetailRepository.getAllGroupedBy(DateUtils.offSetDays(-10));
        list = list.subList(0, list.size() < limit ? list.size() : limit );
        return StringUtils.getKeyValuePairList(list);
    }


    public ErrorType processErrorStack(StatusType type, TestExecution testExecution) {
        return processErrorStack(type, testExecution, null);
    }
    public ErrorType processErrorStack(StatusType type, TestExecution testExecution, TestTriage latestTestTriaged) {

        if (testExecution == null) return ErrorType.NOT_EQUAL;

        ErrorProcessor errorProcessor = errorProcessorMap.getOrDefault(type, new FailErrorProcessor());
        List<StackTrace> stackTracesExecution = errorProcessor.analyzeStackTrace(testExecution.getErrorStackTrace());

        if (stackTracesExecution.isEmpty() && !StringUtils.isEmpty(testExecution.getErrorStackTrace())) {
            return processErrorStackAsString(type, testExecution, latestTestTriaged);
        }

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

        if (latestTestTriaged == null || stackTracesExecution.isEmpty()) return ErrorType.NOT_EQUAL;

        boolean autoTriage = propertyService.valueOf(AUTOTRIAGE_SAME_ERROR_TEST, DEFAULT_AUTOTRIAGE_SAME_ERROR_TEST_ENABLED);
        if (!autoTriage) return ErrorType.UNDEFINED;

        List<StackTrace> stackTracesTriages = errorProcessor.analyzeStackTrace(latestTestTriaged.getTextExecutionStackTrace());
        if (stackTracesTriages.isEmpty() && !StringUtils.isEmpty(latestTestTriaged.getTextExecutionStackTrace())) {
            return processErrorStackAsString(type, testExecution, latestTestTriaged);
        }
        if (stackTracesTriages.isEmpty() || stackTracesTriages.size()!=stackTracesExecution.size()) return ErrorType.NOT_EQUAL;

        int max = propertyService.valueOf(STACK_TRACE_EXCEPTIONS_TO_PROCESS, DEFAULT_STACK_TRACE_EXCEPTIONS_TO_PROCESS);
        max = stackTracesExecution.size() < max ? stackTracesExecution.size() : max;

        ErrorType process = ErrorType.UNDEFINED;
        for(int current = 0; current < max; current++) {
            process = errorProcessor.process(stackTracesExecution.get(current), stackTracesTriages.get(current));
            if(!process.equals(ErrorType.EQUAL)) break;
        }

        return process;

    }

    public ErrorType processErrorStackAsString(StatusType type, TestExecution testExecution, TestTriage latestTestTriaged) {
        if (testExecution == null || latestTestTriaged==null) return ErrorType.NOT_EQUAL;

        ErrorProcessor errorProcessor = errorProcessorMap.getOrDefault(type, new UndefinedErrorProcessor());
        return errorProcessor.process(latestTestTriaged.getTextExecutionErrorDetails(), latestTestTriaged.getTextExecutionStackTrace(), testExecution.getErrorDetails(), testExecution.getErrorStackTrace());
    }


    @Autowired
    public void setErrorProcessorMap(List<ErrorProcessor> errorProcessorMap) {
        this.errorProcessorMap = errorProcessorMap.stream().collect(Collectors.toMap(ErrorProcessor::errorProcessorType, Function.identity()));
    }

    public List<StackTrace> getStackTraces(StatusType status, String stackTrace) {
        ErrorProcessor errorProcessor = errorProcessorMap.getOrDefault(status, new UndefinedErrorProcessor());
        return errorProcessor.analyzeStackTrace(stackTrace);
    }
    
    public void deleteBy(TestTriage testTriage) {
        errorDetailRepository.deleteByPreviousTestTriage(testTriage);
        errorDetailRepository.deleteByTestExecution(testTriage.getTestExecution());
    }
}
