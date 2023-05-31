package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.client.BambooApiClient;
import com.clarolab.bamboo.client.BambooResultClient;
import com.clarolab.bamboo.entities.BambooResult;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Artifact;
import com.clarolab.model.Build;
import com.clarolab.model.Executor;
import com.clarolab.model.Report;
import com.clarolab.model.types.ArtifactType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

@Log
public class BambooResultService {

    private BambooResultClient bambooResultClient;
    private ApplicationContextService context;
    private BambooReportService bambooReportService;

    @Builder
    private BambooResultService(BambooApiClient bambooApiClient, ApplicationContextService applicationContextService){
        bambooResultClient = BambooResultClient.builder().bambooApiClient(bambooApiClient).build();
        this.context = applicationContextService;
        bambooReportService = BambooReportService.builder().context(applicationContextService).build();
    }

    public void setLimit(int limit){
        bambooResultClient.setLimitResults(limit);
    }

    public int getLimit(){
        return bambooResultClient.getLimitResults();
    }

    public List<Build> getResultsForPlanAsBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        return getResultsForPlanAsBuilds(executor, maxBuildsToRetrieve, -1);
    }

    public List<Build> getResultsForPlanAsBuilds(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        try {
            List<Build> builds = Lists.newArrayList();
            bambooResultClient.setLimitResults(maxBuildsToRetrieve);
            List<BambooResult> results = bambooResultClient.getResultsForPlanFromStages(executor.getHiddenData(), greaterThanBuildNumber);
            //Order results by ascending
            results.sort(Comparator.comparing(BambooResult::getBuildNumber));
            for (BambooResult result : results) {
                Build newBuild = createBuild(result);
                if (newBuild != null) {
                    builds.add(newBuild);
                }
            }
            return builds;
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("[getResultsForPlanAsBuilds] : An error occurred trying to get results for Plan(name=%s)", executor.getHiddenData()), e);
            throw new BuildServiceException(String.format("[getResultsForPlanAsBuilds] : An error occurred trying to get results for Plan(name=%s)", executor.getHiddenData()), e);
        }
    }

    public int getLatestResultForPlanAsBuildNumber(Executor executor) throws BuildServiceException {
        try {
            bambooResultClient.setLimitResults(1);
            if (executor.getHiddenData() == null || executor.getHiddenData().isEmpty()) {
                // TODO return 0?
                return -1;
            }
            return bambooResultClient.getResultsForPlanFromStages(executor.getHiddenData()).get(0).getBuildNumber();
        } catch (Exception e) {
            throw new BuildServiceException(String.format("[getLatestResultForPlanAsBuildNumber] : An error occurred trying to get results for Plan(name=%s)", executor.getHiddenData()), e);
        }
    }

    private Build createBuild(BambooResult bambooResult){
        if (bambooResult == null) {
            return null;
        }
        Report report = bambooReportService.getReport(bambooResult.getArtifacts(), String.format("This is a report for Plan(name=%s, build=%s)" , bambooResult.getPlanName(), bambooResult.getBuildResultKey()));

        if (report == null) {
            return null;
        }
        
        Build build = Build.builder()
                .number(bambooResult.getBuildNumber())
                .buildId(String.valueOf(bambooResult.getId()))
                .report(report)
                .artifacts(getArtifacts(bambooResult))
                .executedDate(DateUtils.convertDate(bambooResult.getBuildStartedTime(), DateUtils.DATE_PATTERN_BAMBOO))
                .status(StatusType.getStatus(bambooResult.getBuildState()))
                .displayName(bambooResult.getKey())
                .url(bambooResult.getUrl())
                .populateMode(PopulateMode.PULL)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        context.setUniqueTestCase(build);
        context.setBuild(build);
        context.setReport(report);

        return context.save(build);
    }

    private List<Artifact> getArtifacts(BambooResult bambooResult){
        List<Artifact> artifacts = Lists.newArrayList();
        bambooResult.getArtifacts().stream()
                .filter(artifact -> Artifact.isLogFile(artifact.getResourceExtension()) || Artifact.isImageFile(artifact.getName()))
                .forEach(artifact -> {
                    if(Artifact.isLogFile(artifact.getResourceExtension()))
                        artifacts.add(Artifact.builder().name(artifact.getResourceName()).url(artifact.getResourceLink()).artifactType(ArtifactType.LOG).build());
                    else
                        artifacts.add(Artifact.builder().name(artifact.getResourceName()).url(artifact.getResourceLink()).artifactType(ArtifactType.IMAGE).build());
                });

        return artifacts;
    }

}
