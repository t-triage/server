/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import java.io.Serializable;

public enum ContainerType implements Serializable {

    VIEW(1),
    FOLDER(2),
    PROJECT(3),
    MULTIJOB(4),
    NESTEDVIEW(5);


    private final int containerType;

    ContainerType(int containerType) {
        this.containerType = containerType;
    }

    public int getContainerType() {
        return this.containerType;
    }

    @Override
    public String toString(){
        switch (containerType){
            case 1: return "View";
            case 2: return "Folder";
            case 3: return "Project";
            case 4: return "Multijob";
            case 5: return "NestedView";
            default: return "Unknown";
        }
    }
}
