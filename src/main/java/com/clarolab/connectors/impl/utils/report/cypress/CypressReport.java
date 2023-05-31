package com.clarolab.connectors.impl.utils.report.cypress;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.cypress.json.entity.MainCypress;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

@Log
public class CypressReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private CypressReport(ApplicationContextService context, String applicationTestingEnvironmentVersion) {
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {

        MainCypress mainCypress = com.clarolab.connectors.impl.utils.report.builder.CypressReportBuilder.builder().build().getBuilder().create().fromJson(json, MainCypress.class);
        mainCypress.getSuites().forEach(suite -> suite.setContext(getContext()));

        Report report = Report.builder()
                .type(ReportType.CYPRESS)
                .status(mainCypress.getStatus())
                .passCount(mainCypress.getPassed())
                .failCount(mainCypress.getFailed())
                .skipCount(mainCypress.getSkipped())
                .duration(mainCypress.getDuration())
                .executiondate(0L)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        report.add(mainCypress.getTests(false));

        log.info(String.format("Found CYPRESS report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.CYPRESS);

        return report;
    }

}
