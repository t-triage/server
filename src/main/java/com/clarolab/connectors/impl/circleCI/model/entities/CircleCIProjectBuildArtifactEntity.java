/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Builder
@Data
@Log
public class CircleCIProjectBuildArtifactEntity {

    @Builder.Default
    private List<CircleCIProjectBuildArtifactElementEntity> circleCIProjectBuildArtifactElementEntities = Lists.newArrayList();

    public CircleCIProjectBuildArtifactElementEntity getArtifactWithTestReport(){
        return circleCIProjectBuildArtifactElementEntities.stream().filter(CircleCIProjectBuildArtifactElementEntity::itContainsTestReport).findFirst().orElse(null);
    }

    public CircleCIProjectBuildArtifactElementEntity getArtifactWithApplicationTestingEnvironmentVersion(){
        return circleCIProjectBuildArtifactElementEntities.stream().filter(CircleCIProjectBuildArtifactElementEntity::itContainsApplicationTestingEnvironmentVersionFile).findFirst().orElse(null);
    }

}
