package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Builder
@Data
public class AllureMapEntity {

    private String name;
    private String value;
    private String source;
}
