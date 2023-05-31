/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.processors;

import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
public class FixedStateProcessor extends PassStateProcessor implements StateProcessor {

    @Override
    public StatusType processType() {
        return StatusType.FIXED;
    }
}
