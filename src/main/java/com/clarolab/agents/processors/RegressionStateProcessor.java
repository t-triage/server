/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.processors;

import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
public class RegressionStateProcessor extends FailStateProcessor {

    @Override
    public StatusType processType() {
        return StatusType.REGRESSION;
    }
}
