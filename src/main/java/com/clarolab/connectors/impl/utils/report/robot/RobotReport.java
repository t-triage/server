package com.clarolab.connectors.impl.utils.report.robot;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.builder.RobotReportBuilder;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.MainRobot;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.extern.java.Log;

import static com.clarolab.util.StringUtils.parseDataError;

@Log
public class RobotReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder(builderClassName = "createBuilder")
    private RobotReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    @Override
    public Report createReport(String content, String observation) throws ReportServiceException {
        MainRobot report = RobotReportBuilder.builder().build().getBuilder().create().fromJson(content, MainRobot.class);

        if(report == null || report.isEmpty()) {
            throw ReportServiceException.builder().message(parseDataError("Error getting report", observation)).reason("Reason: " + StatusType.getStatus(StatusType.UNKNOWN)).type(ReportType.ROBOT).build();
        }
        report.setContext(getContext());
        Report reportToReturn = Report.builder()
                .type(ReportType.ROBOT)
                .description(observation)
                .duration(report.getDuration())
                .passCount(report.getPassCount())
                .failCount(report.getFailCount())
                .skipCount(report.getSkipCount())
                .status(report.getStatus())
                .executiondate(report.getExecutedDate())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        reportToReturn.add(report.getTests());

        log.info(String.format("Found ROBOT report for %s", observation));
        getContext().configureRecentlyUsedReport(ReportType.ROBOT);
        return reportToReturn;
    }
}
