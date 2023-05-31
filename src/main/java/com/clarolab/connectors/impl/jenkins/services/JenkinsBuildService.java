
package com.clarolab.connectors.impl.jenkins.services;

import com.clarolab.client.JenkinsApiClient;
import com.clarolab.client.JenkinsJobClient;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsBuild;
import com.clarolab.entities.JenkinsJob;
import com.clarolab.model.Artifact;
import com.clarolab.model.Build;
import com.clarolab.model.Report;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.ArtifactType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.utils.JenkinsConstants;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Log
public class JenkinsBuildService {

    private JenkinsJobClient jenkinsJobClient;
    private ApplicationContextService context;

    private JenkinsReportService jenkinsReportService;

    @Builder
    private JenkinsBuildService(JenkinsApiClient jenkinsApiClient, ApplicationContextService context){
        this.jenkinsJobClient = JenkinsJobClient.builder().jenkinsApiClient(jenkinsApiClient).build();
        this.context = context;
        this.jenkinsReportService = jenkinsReportService.builder().context(context).build();
    }

    public Build getBuild(String jobUrl, int buildNumber) throws BuildServiceException{
        JenkinsJob job;
        try {
            job = jenkinsJobClient.getJob(jobUrl);
        } catch (Exception e) {
            throw new BuildServiceException(String.format("[getBuild] : An error occurred trying to get build#%d for JenkinsJob(url=%s)]", buildNumber, jobUrl), e);
        }
        return getBuild(job, buildNumber);
    }

    public Build getBuild(JenkinsJob job, int buildNumber) throws BuildServiceException{
        try {
            return createBuild(job.getBuildAtPosition(buildNumber));
        } catch (Exception e) {
            throw new BuildServiceException(String.format("[getBuild] : An error occurred trying to get build#%d for JenkinsJob(url=%s)]", buildNumber, job.getUrl()), e);
        }
    }

    public List<Build> getBuilds(String jobUrl, int limitOfBuilds, int greaterThanBuildNumber) throws BuildServiceException{
        JenkinsJob job;
        try {
            job = jenkinsJobClient.getJob(jobUrl);
        } catch (Exception e) {
            throw new BuildServiceException(String.format("[getBuilds] : An error occurred trying to get builds for JenkinsJob(url=%s)]", jobUrl), e);
        }
        return getBuilds(job, limitOfBuilds, greaterThanBuildNumber);
    }

    public List<Build> getBuilds(JenkinsJob job, int limitOfBuilds, int greaterThanBuildNumber) throws BuildServiceException{
        try {
            List<JenkinsBuild> builds = job.setLimitOfBuildsTobeRecovered(limitOfBuilds).setBuildsGreaterThanThisNumber(greaterThanBuildNumber).getBuilds();
            List<Build> toReturn = Lists.newArrayList();
            builds.forEach(build -> toReturn.add(createBuild(build)));
            return toReturn;
        }catch(Exception e){
            throw new BuildServiceException(String.format("[getBuildsForJob] : An error occurred trying to get builds for JenkinsJob(url=%s)]", job.getUrl()), e);
        }
    }

    // *****************************************************************************************************************
    // ************************************************* Utilities *****************************************************
    // *****************************************************************************************************************

    private Build createBuild(JenkinsBuild jenkinsBuild) {
//        log.info("::::::::::Creating build::::::::::\n"+jenkinsBuild.toString());
        if (jenkinsBuild.getNumber() <= context.getLatestBuildOnDB()) {
            return null;
        }
        Build buildOut =  Build.builder()
                .number(jenkinsBuild.getNumber())
                .buildId(jenkinsBuild.getId())
                .displayName(jenkinsBuild.getFullDisplayName())
                .url(jenkinsBuild.getUrl())
                .executedDate(jenkinsBuild.getTimestamp())
                .status(StatusType.getStatus(jenkinsBuild.getResult()))
                .artifacts(getArtifacts(jenkinsBuild))
                .enabled(true)
                .populateMode(PopulateMode.PULL)
                .timestamp(DateUtils.now())
                .build();

        List<Report> reports = new ArrayList<Report>(jenkinsReportService.getReportData(jenkinsBuild));
        for (Report r : reports){
            r.updateExecutionDateIfIsNeeded(buildOut.getExecutedDate());

            context.setBuild(buildOut);
            context.setReport(r);

            //Associate image, video, log from artifact to test case
            JenkinsArtifactsService  jenkinsArtifactsService = JenkinsArtifactsService.builder().build();
            for(TestExecution testcase: r.getTestExecutions()){
                testcase.setScreenshotURL(jenkinsArtifactsService.getImageArtifact(testcase, buildOut.getArtifacts()));
                testcase.setVideoURL(jenkinsArtifactsService.getVideoArtifact(testcase, buildOut.getArtifacts()));
            }

            buildOut.setReport(r);
        }
        return context.save(buildOut);

    }

    private List<Artifact> getArtifacts(JenkinsBuild jenkinsBuild){
        List<Artifact> artifacts = Lists.newArrayList();
        artifacts.add(Artifact.builder().name("output.log").url(jenkinsBuild.getUrl()+"consoleFull").artifactType(ArtifactType.STANDARD_OUTPUT).build());

        jenkinsBuild.getArtifacts().stream().filter(artifact -> Artifact.isImageFile(artifact.getRelativePath())).forEach(image -> {
            artifacts.add(Artifact.builder().name(image.getFileName()).url(jenkinsBuild.getUrl()+ JenkinsConstants.JENKINS_ARTIFACT_ENDPOINT +image.getRelativePath()).artifactType(ArtifactType.IMAGE).build());
        });

        return artifacts;
    }

    private boolean isValid(JenkinsBuild jenkinsBuild){
        return jenkinsBuild.getResult() != null && (
                jenkinsBuild.getResult().equals("SUCCESS") ||
                        jenkinsBuild.getResult().equals("FAILURE") ||
                        jenkinsBuild.getResult().equals("UNSTABLE"));
    }


}
