/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.errors;

import com.clarolab.model.types.StatusType;
import org.springframework.stereotype.Component;

@Component
public class FailErrorProcessor implements ErrorProcessor {

    /* FailErrorProcessor should be the default one for now*/

    @Override
    public StatusType errorProcessorType() {
        return StatusType.FAIL;
    }
}
