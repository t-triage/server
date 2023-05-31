/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class JunitBase extends AbstractTestCreator {

    private String name;
    private long time;
    private String group;

    public String getID(){
        return this.name + "#" + this.hashCode();
    }
}
