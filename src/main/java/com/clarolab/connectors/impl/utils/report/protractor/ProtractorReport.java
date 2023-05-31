package com.clarolab.connectors.impl.utils.report.protractor;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.MainProtractor;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

@Log
public class ProtractorReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private ProtractorReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }


    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {
        MainProtractor mainProtractor = com.clarolab.connectors.impl.utils.report.builder.ProtractorReportBuilder.builder().build().getBuilder().create().fromJson(json, MainProtractor.class);
        mainProtractor.getSuites().forEach(suite ->  suite.setContext(getContext()));

        Report report =  Report.builder()
                .type(ReportType.PROTRACTOR)
                .description(observations)
                .status(mainProtractor.getStatus())
                .executiondate(0L)
                .passCount(mainProtractor.getPassed())
                .failCount(mainProtractor.getFailed())
                .skipCount(mainProtractor.getSkipped())
                .duration(mainProtractor.getDuration())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(mainProtractor.getTests());

        log.info(String.format("Found PROTRACTOR report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.PROTRACTOR);
        return report;
    }
}
