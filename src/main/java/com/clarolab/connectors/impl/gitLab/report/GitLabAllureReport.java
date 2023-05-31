package com.clarolab.connectors.impl.gitLab.report;

import com.clarolab.connectors.impl.utils.report.allure.json.entity.MainAllure;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.FileUtils;
import lombok.Builder;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;

@Log
@Builder
public class GitLabAllureReport {

    private MainAllure mainAllure;
    private ApplicationContextService context;


    public Report getReport(File file) throws IOException {
        mainAllure = MainAllure.builder()
                .tests(FileUtils.getFiles(file, "/allure-report/data/test-cases"))
                .summary(FileUtils.getFile(file, "/allure-report/export", "summary.json"))
                .context(context)
                .build();

        Report report = Report.builder()
                .type(ReportType.ALLURE)
                .executiondate(mainAllure.getExecutionDate())
                .status(mainAllure.getStatus())
                .duration(mainAllure.getDuration())
                .passCount(mainAllure.getPassed())
                .skipCount(mainAllure.getSkipped())
                .failCount(mainAllure.getFailed())
                .timestamp(DateUtils.now())
                .enabled(true).build();

        mainAllure.getTests().forEach(test -> report.add(test));

        return report;
    }
}
