/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Log
@NoArgsConstructor
@AllArgsConstructor
public abstract class CucumberBase extends AbstractTestCreator {

    private String id;
    private String name;
    private String description;
    private List<String> tags;

}
