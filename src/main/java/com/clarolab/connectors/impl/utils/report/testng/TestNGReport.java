package com.clarolab.connectors.impl.utils.report.testng;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.testng.json.entity.MainTestNG;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.extern.java.Log;
import org.json.XML;

@Log
public class TestNGReport extends AbstractReport {

    private String applicationTestingEnvironmentVersion;
    private MainTestNG mainTestNG;

    @Builder
    private TestNGReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    public boolean isReportNotFound(){
        return mainTestNG == null;
    }


    private MainTestNG getMainTestNG(String testNgReportContent) {
        String json = StringUtils.isJson(testNgReportContent) ? testNgReportContent : XML.toJSONObject(testNgReportContent).toString(4);
        MainTestNG mainTestNG = com.clarolab.connectors.impl.utils.report.builder.TestngReportBuilder.builder().build().getBuilder().create().fromJson(json, MainTestNG.class);
        mainTestNG.setContext(getContext());
        return mainTestNG;
    }


    //***********************************************************************************************************************************
    //***********************************************************************************************************************************

    @Override
    public Report createReport(String content, String observation) {
        mainTestNG = getMainTestNG(content);

        if(isReportNotFound())
            return Report.getDefault();

        Report reportToReturn = Report.builder()
                .type(ReportType.TESTNG)
                .description(observation)
                .duration(mainTestNG.getDuration())
                .passCount(mainTestNG.getPassed())
                .failCount(mainTestNG.getFailed())
                .skipCount( mainTestNG.getSkipped())
                .status(mainTestNG.getStatus())
                .executiondate(mainTestNG.getExecutionDate())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        reportToReturn.add(mainTestNG.getTests());

        log.info(String.format("Found TESTNG report for %s", observation));
        getContext().configureRecentlyUsedReport(ReportType.TESTNG);
        return reportToReturn;
    }
}
