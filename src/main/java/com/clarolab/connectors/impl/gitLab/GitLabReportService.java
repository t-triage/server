/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.gitLab;

import com.clarolab.connectors.impl.gitLab.report.GitLabAllureReport;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.util.CompressUtils;
import com.clarolab.util.Constants;
import com.clarolab.util.FileUtils;
import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.io.FilenameUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Log
@Builder
public class GitLabReportService {

    private GitLabApi gitLabApiClient;
    private ApplicationContextService context;

    public Report getReport(Integer projectID, Job job){
        File file = null;
        File savedArtifact = null;
        try {
            if(job.getArtifactsFile() != null){
                //TODO: Download only files related with automation report. Not other like war files.
                file = gitLabApiClient.getJobApi().downloadArtifactsFile(projectID, job.getId(), new File(Constants.DEFAULT_TEMP_DIR));
                String commonFileName = String.format("%s_%s_%s_%s",file.getName(), projectID, job.getId(), StringUtils.randomString(10));
                file = FileUtils.rename(file, String.format("%s.%s",commonFileName, FilenameUtils.getExtension(file.getName())));
                file.deleteOnExit();
                savedArtifact = CompressUtils.unZipAndRename(file,Constants.DEFAULT_TEMP_DIR, commonFileName);
                FileUtils.deleteOnExit(savedArtifact);
                log.log(Level.INFO, "To analise artifact " + savedArtifact.getAbsolutePath());

                return getReport(savedArtifact);
            }

        } catch (GitLabApiException | IOException e) {
            log.log(Level.SEVERE, String.format("[getReport] : Can not get report for resource project#%d and job#%d.\n Cause: %s", projectID, job.getId(), e.getMessage()), e);
        }finally {
            if(file != null){
                file.delete();
            }
            if(savedArtifact != null){
                 FileUtils.delete(savedArtifact);
            }
        }

        return Report.getDefault();
    }

    private Report getReport(File file) throws IOException {
        if(FileUtils.itContainsFolder(file, "allure-report")) {
            return GitLabAllureReport.builder().context(context).build().getReport(file);
        }
        return null;
    }
}
