/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum ArtifactType {

    IMAGE(1),
    VIDEO(2),
    LOG(3),
    STANDARD_OUTPUT(4);

    private final int artifactType;

    ArtifactType(int artifactType){
        this.artifactType = artifactType;
    }

    public int getArtifactType(){
        return this.artifactType;
    }
}
