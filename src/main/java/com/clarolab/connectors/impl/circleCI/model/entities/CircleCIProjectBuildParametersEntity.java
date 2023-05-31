/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Builder
@Data
@Log
public class CircleCIProjectBuildParametersEntity {

    private String CIRCLE_JOB;
}
