package com.clarolab.connectors.impl.utils.report.allure;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.allure.json.entity.MainAllure;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.Builder;

public class AllureReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private AllureReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {
        MainAllure mainAllure = MainAllure.builder().singleTest(json).context(getContext()).isForDebug(false).build();

        Report report = Report.builder()
                .type(ReportType.ALLURE)
                .executiondate(mainAllure.getExecutionDate())
                .status(mainAllure.getStatus())
                .duration(mainAllure.getDuration())
                .passCount(mainAllure.getPassed())
                .skipCount(mainAllure.getSkipped())
                .failCount(mainAllure.getFailed())
                .productVersion(applicationTestingEnvironmentVersion)
                .timestamp(DateUtils.now())
                .enabled(true).build();

        report.add(mainAllure.getTests());

        return report;
    }
}
