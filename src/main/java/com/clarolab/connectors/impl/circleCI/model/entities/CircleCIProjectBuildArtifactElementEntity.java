/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import com.clarolab.model.types.ReportType;
import com.clarolab.util.LogicalCondition;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Builder
@Data
@Log
public class CircleCIProjectBuildArtifactElementEntity {

    private String path;
    private String pretty_path;
    private String url;
    private int node_index;

    public boolean itContainsTestReport(){
        return LogicalCondition.OR(itContainsTestNGFile(), itContainsRobotFile(), itContainsCucumberFile(), itContainsJunitFile());
    }

    public ReportType getReportTypeFromTestReport(){
        if(url.endsWith("testng-results.xml"))  return ReportType.TESTNG;
        if(url.endsWith("output.xml"))  return ReportType.ROBOT;
        if(url.endsWith("cucumber-report.json"))  return ReportType.CUCUMBER;
        if(url.endsWith("junit-results.xml"))  return ReportType.JUNIT;
        return ReportType.UNKNOWN;
    }

    public boolean itContainsTestNGFile(){
        return url.endsWith("testng-results.xml");
    }

    public boolean itContainsRobotFile(){
        return url.endsWith("output.xml");
    }

    public boolean itContainsCucumberFile(){
        return url.endsWith("cucumber-report.json");
    }

    public boolean itContainsJunitFile(){
        return url.endsWith("junit-results.xml");
    }

    public boolean itContainsApplicationTestingEnvironmentVersionFile(){
        return url.matches("(\\W|\\w)*(V|v)ersion\\.json");
    }

    public String baseUrl(){
        return this.url.split("/"+this.node_index+"/")[0];

    }

    public String getFilePathRequest(){
        return "/"+this.node_index+"/" + this.url.split("/"+this.node_index+"/")[1];
    }

    public String getFileRequest(){
        return getFilePathRequest() + "?circle-token=%s";
    }

}
