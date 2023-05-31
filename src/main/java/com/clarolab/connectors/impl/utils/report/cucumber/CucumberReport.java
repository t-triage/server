package com.clarolab.connectors.impl.utils.report.cucumber;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.cucumber.json.entity.MainCucumber;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

@Log
public class CucumberReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private CucumberReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String json, String observations){
        MainCucumber mainCucumber = com.clarolab.connectors.impl.utils.report.builder.CucumberReportBuilder.builder().build().getBuilder().create().fromJson(json, MainCucumber.class);
        mainCucumber.getSuites().forEach(suite -> suite.setContext(getContext()));

        Report report =  Report.builder()
                .type(ReportType.CUCUMBER)
                .description(observations)
                .status(mainCucumber.getStatus())
                .executiondate(0L)
                .passCount(mainCucumber.getPassed())
                .failCount(mainCucumber.getFailed())
                .skipCount(mainCucumber.getSkipped())
                .duration(mainCucumber.getDuration())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(mainCucumber.getTests());

        log.info(String.format("Found CUCUMBER report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.CUCUMBER);
        return report;
    }
}
