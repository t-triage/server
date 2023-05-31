/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.errors;

import com.clarolab.model.helper.exparser.StackTrace;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
public class UndefinedErrorProcessor implements ErrorProcessor {

    @Override
    public StatusType errorProcessorType() {
        return StatusType.UNKNOWN;
    }

    @Override
    public ErrorType process(StackTrace stackTraceLastTriage, StackTrace stackTraceTestExecution) {
        return ErrorType.UNDEFINED;
    }
}
