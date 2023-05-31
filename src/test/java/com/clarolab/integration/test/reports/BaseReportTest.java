/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.runner.category.IntegrationTestCategory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.json.XML;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Data
@Log
@Category(IntegrationTestCategory.class)
public abstract class BaseReportTest {

    private String basePath = "reports/";
    private String cucumberBasePath = basePath+"cucumber/";
    private String junitBasePath = basePath+"junit/";
    private String testngBasePath = basePath+"testng/";
    private String robotBasePath = basePath+"robot/";
    private String protractorBasePath = basePath+"protractor/";
    private String allureBasePath = basePath+"allure/";
    private String cypressBasePath = basePath+"cypress/";
    private String jestBasePath = basePath+"jest/";
    private String pythonBasePath = basePath+"python/";

    protected List<String> junitFiles = Lists.newArrayList(junitBasePath+"junit1.xml",junitBasePath+"junit2.xml", junitBasePath+"junit3.xml", junitBasePath+"junit4.xml",
            junitBasePath+"junit5.xml", junitBasePath+"junit6.xml", junitBasePath+"junit7.xml", junitBasePath+"junit8.xml", junitBasePath+"junit9.xml", junitBasePath+"junit10.xml",
            junitBasePath+"junit11.xml", junitBasePath+"junit12.xml");
    protected List<String> cucumberFiles = Lists.newArrayList(cucumberBasePath+"cucumber1.json", cucumberBasePath+"cucumber2.json", cucumberBasePath+"cucumber3.json",
            cucumberBasePath+"cucumber4.json");
    protected List<String> testngFiles = Lists.newArrayList(testngBasePath+"testng1.xml", testngBasePath+"testng2.xml", testngBasePath+"testng3.xml", testngBasePath+"testng4.xml",
            testngBasePath+"testng5.xml", testngBasePath+"testng6.xml", testngBasePath+"testng7.xml", testngBasePath+"testng8.xml", testngBasePath+"testng9.xml");
    protected List<String> robotFiles = Lists.newArrayList(robotBasePath+"output1.xml", robotBasePath+"output2.xml", robotBasePath+"output3.xml", robotBasePath+"output4.xml",
            robotBasePath+"output5.xml", robotBasePath+"output6.xml", robotBasePath+"output7.xml", robotBasePath+"output8.xml");
    protected List<String> protractorFiles = Lists.newArrayList(protractorBasePath+"protractor1.xml", protractorBasePath+"protractor2.xml");
    protected List<String> protractorV2Files = Lists.newArrayList(protractorBasePath+"protractor3.xml");
    protected List<String> allureFiles = Lists.newArrayList(allureBasePath+"allure1.json", allureBasePath+"allure2.json", allureBasePath+"allure3.json");
    protected List<String> cypressFiles = Lists.newArrayList(cypressBasePath+"cypress1.json", cypressBasePath+"cypress2.json", cypressBasePath+"cypress3.json");
    protected List<String> jestFiles = Lists.newArrayList(jestBasePath+"jest1.json");

    protected List<String> pythonFiles = Lists.newArrayList(pythonBasePath+"python1.json");


    Map<String, InputStream> reports;

    private Map<String, InputStream> getReport(String type){
        reports = Maps.newHashMap();
        List<String> collection = null;
        if("junit".equals(type.toLowerCase()))
            collection = junitFiles;
        else if("testng".equals(type.toLowerCase()))
            collection = testngFiles;
        else if("cucumber".equals(type.toLowerCase()))
            collection = cucumberFiles;
        else if("robot".equals(type.toLowerCase()))
            collection = robotFiles;
        else if("protractor".equals(type.toLowerCase()))
            collection = protractorFiles;
        else if("protractorv2".equals(type.toLowerCase()))
            collection = protractorV2Files;
        else if("allure".equals(type.toLowerCase()))
            collection = allureFiles;
        else if("cypress".equals(type.toLowerCase()))
            collection = cypressFiles;
        else if("jest".equals(type.toLowerCase()))
            collection = jestFiles;
        else if("python".equals(type.toLowerCase()))
            collection = pythonFiles;
        for(String file: collection){
            reports.put(file, getClass().getClassLoader().getResourceAsStream(file));
        }
        return reports;
    }

    public InputStream getReport(String type, String key){
        return this.getReport(type).get(key);
    }

    public String getReportContentFromJson(InputStream inputStream){
        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from json", e);
            return null;
        }
    }

    public String getReportContentFromXml(InputStream inputStream){
        try {
            return XML.toJSONObject(IOUtils.toString(inputStream, Charset.defaultCharset())).toString(4);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from xml", e);
            return null;
        }
    }



}
