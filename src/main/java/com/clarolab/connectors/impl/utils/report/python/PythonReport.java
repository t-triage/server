package com.clarolab.connectors.impl.utils.report.python;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.cypress.json.entity.MainCypress;
import com.clarolab.connectors.impl.utils.report.python.json.entity.MainPython;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

@Log
public class PythonReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private PythonReport(ApplicationContextService context, String applicationTestingEnvironmentVersion) {
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {

        MainPython mainPython = com.clarolab.connectors.impl.utils.report.builder.PythonReportBuilder.builder().build().getBuilder().create().fromJson(json, MainPython.class);
        mainPython.getSuites().forEach(suite -> suite.setContext(getContext()));

        Report report = Report.builder()
                .type(ReportType.PYTHON)
                .status(mainPython.getStatus())
                .passCount(mainPython.getPassed())
                .failCount(mainPython.getFailed())
                .warningCount(mainPython.getWarning())
                .duration(mainPython.getDuration())
                .executiondate(0L)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        report.add(mainPython.getTests(false));

        log.info(String.format("Found PYTHON report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.PYTHON);

        return report;
    }

}