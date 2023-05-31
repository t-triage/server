/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Data
public class TestNGSuiteTestClassTestMethod extends TestNGBase{

    @JsonIgnore
    private static List<String> assumptions = Lists.newArrayList("AssumptionViolatedException");

    private String reporter_output;
    private String signature;
    private StatusType status;
    private boolean is_config;
    private String data_provider;
    private List<TestNGSuiteTestClassTestMethodParameter> parameters;
    private TestNGSuiteTestClassTestMethodException exception;

    @Builder
    private TestNGSuiteTestClassTestMethod(String name, String started_at, String finished_at, long duration_ms, String reporter_output, String signature, StatusType status, TestNGSuiteTestClassTestMethodException exception, boolean is_config, String data_provider, List<TestNGSuiteTestClassTestMethodParameter> parameters){
        super(name, started_at, finished_at, duration_ms);
        this.reporter_output = reporter_output;
        this.signature = signature;
        this.status = status;
        this.exception = exception;
        this.is_config = is_config;
        this.data_provider = data_provider;
        this.parameters = parameters;
    }

    public String getError(){
        return this.exception == null ?
                null :
                this.exception.getClazz() +
                        (!Strings.isNullOrEmpty(this.exception.getMessage()) ? StringUtils.getLineSeparator() +this.exception.getMessage() : "");
    }

    public String getErrorDetails(){
        return this.exception == null ? null : this.exception.getFull_stacktrace();
    }

    public StatusType getStatus(){
        if(isSkippedByAssumption())
            return StatusType.SKIP;

        return this.status;
    }

    public boolean isSkippedByAssumption(){
        return this.getException() != null && assumptions.stream().filter(element -> this.getException().getClazz().contains(element)).count() > 0;
    }

    public String getName(){
        if(CollectionUtils.isEmpty(parameters))
            return super.getName();
        else {
            StringBuffer name = new StringBuffer(super.getName());
            name.append("(");
            parameters.stream().sorted(Comparator.comparing(TestNGSuiteTestClassTestMethodParameter::getPosition))
                    .forEach(parameter -> name.append(parameter.getValue()).append(","));
            name.replace(name.length()-1, name.length(), ")");
            return name.toString();
        }
    }

}
