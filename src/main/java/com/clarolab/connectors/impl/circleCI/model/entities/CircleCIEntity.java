/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Builder
@Log
public class CircleCIEntity {

    @Builder.Default
    private List<CircleCIProjectEntity> circleCIProjectDataList = Lists.newArrayList();
}
