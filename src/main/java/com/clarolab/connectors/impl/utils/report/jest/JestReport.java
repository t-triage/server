package com.clarolab.connectors.impl.utils.report.jest;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.jest.json.entity.MainJest;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

@Log
public class JestReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private JestReport(ApplicationContextService context, String applicationTestingEnvironmentVersion) {
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {

        MainJest mainJest = com.clarolab.connectors.impl.utils.report.builder.JestReportBuilder.builder().build().getBuilder().create().fromJson(json, MainJest.class);
        mainJest.getSuites().forEach(suite -> suite.setContext(getContext()));

        Report report = Report.builder()
                .type(ReportType.JEST)
                .status(mainJest.getStatus())
                .passCount(mainJest.getPassed())
                .failCount(mainJest.getFailed())
                .skipCount(mainJest.getSkipped())
                .duration(mainJest.getDuration()/1000)
                .executiondate(0L)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        report.add(mainJest.getTests(false));

        log.info(String.format("Found JEST report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.JEST);

        return report;
    }

}
