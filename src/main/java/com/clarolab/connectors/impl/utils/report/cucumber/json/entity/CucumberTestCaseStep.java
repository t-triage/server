/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.model.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@Log
@Builder
public class CucumberTestCaseStep {

    private String name;
    private String keyword;
    private String matchLocation;
    private long duration;
    private CucumberTestCaseStatus status;
    private List<CucumberTestCaseStepParameters> parameters;

    public StatusType getStatus(){
        return this.status.getStatus();
    }

    public String getError(){
        return this.status.getError();
    }

    public String getParameters(){
        StringBuilder value = new StringBuilder();
        if(CollectionUtils.isEmpty(parameters))
            return value.toString();
        parameters.forEach(p -> value.append(p.getValue()).append(","));
        return value.reverse().deleteCharAt(0).reverse().toString();
    }

}
