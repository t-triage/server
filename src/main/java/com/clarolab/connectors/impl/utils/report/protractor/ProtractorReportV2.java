package com.clarolab.connectors.impl.utils.report.protractor;

import com.clarolab.connectors.impl.AbstractReport;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.MainProtractorV2;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Log
public class ProtractorReportV2 extends AbstractReport {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private ProtractorReportV2(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        setContext(context);
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }


    @Override
    public Report createReport(String json, String observations) throws ReportServiceException {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        MainProtractorV2 mainProtractorV2 = null;
        try {
            mainProtractorV2 = com.clarolab.connectors.impl.utils.report.builder.ProtractorV2ReportBuilder.builder().build().getBuilder().create().fromJson(json, MainProtractorV2.class);
        } catch (Exception ex) {
            log.log(Level.WARNING, "Could not parse: " + json, ex);
        }
        if (mainProtractorV2 == null) {
            return null;
        }
        mainProtractorV2.setContext(getContext());
        mainProtractorV2.setScreenshots(getScreenshots());

        Report report =  Report.builder()
                .type(ReportType.PROTRACTOR)
                .description(observations)
                .status(mainProtractorV2.getStatus())
                .executiondate(0L)
                .passCount(mainProtractorV2.getPassed())
                .failCount(mainProtractorV2.getFailed())
                .skipCount(mainProtractorV2.getSkipped())
                .duration(mainProtractorV2.getDuration())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(mainProtractorV2.getTestCase());

        log.info(String.format("Found PROTRACTOR report for %s", observations));
        getContext().configureRecentlyUsedReport(ReportType.PROTRACTOR);
        return report;
    }

    public Map<String, String> getScreenshots() {
        // Test Name, URL
        return new HashMap<>();
    }
}
