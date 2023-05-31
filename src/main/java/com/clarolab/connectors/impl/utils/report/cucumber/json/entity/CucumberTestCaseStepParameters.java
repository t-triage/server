package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CucumberTestCaseStepParameters {

    private String value;
}
