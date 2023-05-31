/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate.util;

import com.clarolab.model.types.ReportType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.json.XML;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public final class ReportsTestHelper {

    private static String basePath = "reports/";
    private static String cucumberBasePath = basePath+"cucumber/";
    private static String junitBasePath = basePath+"junit/";
    private static String testngBasePath = basePath+"testng/";
    private static String robotBasePath = basePath+"robot/";
    private static String protractorBasePath = basePath+"protractorSteps/";

    private static List<String> junitFiles = Lists.newArrayList(junitBasePath+"junit1.xml",junitBasePath+"junit2.xml", junitBasePath+"junit3.xml", junitBasePath+"junit4.xml",
            junitBasePath+"junit5.xml", junitBasePath+"junit6.xml", junitBasePath+"junit7.xml", junitBasePath+"junit8.xml", junitBasePath+"junit9.xml", junitBasePath+"junit10.xml");
    private static List<String> cucumberFiles = Lists.newArrayList(cucumberBasePath+"cucumber1.json", cucumberBasePath+"cucumber2.json", cucumberBasePath+"cucumber3.json");
    private static List<String> testngFiles = Lists.newArrayList(testngBasePath+"testng1.xml", testngBasePath+"testng2.xml", testngBasePath+"testng3.xml", testngBasePath+"testng4.xml",
            testngBasePath+"testng5.xml", testngBasePath+"testng6.xml");
    private static List<String> robotFiles = Lists.newArrayList(robotBasePath+"output1.xml", robotBasePath+"output2.xml", robotBasePath+"output3.xml", robotBasePath+"output4.xml",
            robotBasePath+"output5.xml", robotBasePath+"output6.xml", robotBasePath+"output7.xml");
    private static List<String> protractorFiles = Lists.newArrayList(protractorBasePath+"acton1.json", protractorBasePath+"acton2.json", protractorBasePath+"acton3.json", protractorBasePath+"acton4.json", protractorBasePath+"acton5.json", protractorBasePath+"acton6.json", protractorBasePath+"acton7.json");

    private static Map<ReportType, List<InputStream>> reports;

    private static Map<ReportType, List<InputStream>> getReports() {
        if (reports == null) {
            reports = Maps.newHashMap();

            List<InputStream> isJunit = junitFiles
                    .stream()
                    .map(s -> ReportsTestHelper.class.getClassLoader().getResourceAsStream(s))
                    .collect(Collectors.toList());
            List<InputStream> isTestNG = testngFiles
                    .stream()
                    .map(s -> ReportsTestHelper.class.getClassLoader().getResourceAsStream(s))
                    .collect(Collectors.toList());
            List<InputStream> isCucumber = cucumberFiles
                    .stream()
                    .map(s -> ReportsTestHelper.class.getClassLoader().getResourceAsStream(s))
                    .collect(Collectors.toList());
            List<InputStream> isRobot = robotFiles
                    .stream()
                    .map(s -> ReportsTestHelper.class.getClassLoader().getResourceAsStream(s))
                    .collect(Collectors.toList());

            List<InputStream> isProtractor = protractorFiles
                    .stream()
                    .map(s -> ReportsTestHelper.class.getClassLoader().getResourceAsStream(s))
                    .collect(Collectors.toList());

            reports.put(ReportType.JUNIT, isJunit);
            reports.put(ReportType.TESTNG, isTestNG);
            reports.put(ReportType.CUCUMBER, isCucumber);
            reports.put(ReportType.ROBOT, isRobot);
            reports.put(ReportType.PROTRACTOR_STEPS, isProtractor);


        }
        return reports;
    }

    public static String getReportContentFromJson(InputStream inputStream){
        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from json", e);
            return null;
        }
    }

    public static String getReportContentFromXml(InputStream inputStream){
        try {
            return XML.toJSONObject(IOUtils.toString(inputStream, Charset.defaultCharset())).toString(4);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from xml", e);
            return null;
        }
    }

    public static String getPlainReport(InputStream inputStream){
        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from InputStream", e);
            return null;
        }
    }

    public static String getRandomJUnitReport() {
        List<InputStream> inputStreams = getReports().get(ReportType.JUNIT);
        Optional<InputStream> any = inputStreams.stream().findAny();
        return getPlainReport(any.get());
    }

    public static String getReportContentFromXml(ReportType reportType) {
        List<InputStream> inputStreams = getReports().get(reportType);
        Optional<InputStream> any = inputStreams.stream().findAny();
        return getReportContentFromXml(any.get());
    }

    public static String getPlainFile(ReportType reportType, int index){
        try {
            return IOUtils.toString(getReports().get(reportType).get(index), Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Error getting content from InputStream %s %d", reportType, index), e);
            return null;
        }
    }
}
