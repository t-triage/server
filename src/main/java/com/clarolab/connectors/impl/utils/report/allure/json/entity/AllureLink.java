package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.extern.java.Log;

@Log
@Builder
public class AllureLink {

    private String name;
    private String url;
    private String type;
}
