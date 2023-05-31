package com.clarolab.connectors.impl.gitLab;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Artifact;
import com.clarolab.model.Build;
import com.clarolab.model.Report;
import com.clarolab.model.types.ArtifactType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Job;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Builder
public class GitLabBuildService {

    private GitLabApi gitLabApiClient;
    private GitLabReportService gitLabReportService;
    private ApplicationContextService context;

    public List<Build> getBuildsForJobs(Integer projectId, List<Job> jobs){
        return getBuildsForJobs(projectId, jobs, 0, true);
    }

    public List<Build> getBuildsForJobs(Integer projectId, List<Job> jobs, int maxBuildsToRetrieve, boolean save){
        List<Build> builds = Lists.newArrayList();

        //If a job is manually retried, there will be two builds for same job in same pipeline.
        //Discard the old one and get only the newest.
        List<Job> removeDuplicated = Lists.newArrayList();
        jobs.stream().collect(Collectors.groupingBy(j -> j.getPipeline().getId())).forEach((k,v) -> removeDuplicated.add(v.get(0)));
        jobs.clear();jobs.addAll(removeDuplicated);

        if(maxBuildsToRetrieve == 0) {
            //here means no limit
            jobs.stream().forEach(job -> {
                Build build = createBuild(projectId, job);
                if(build != null)
                    builds.add(build);
            });
        } else
            jobs.stream().limit(maxBuildsToRetrieve).forEach(job -> {
                Build build = createBuild(projectId, job);
                if(build != null)
                    builds.add(build);
            });

        context.sortBuildsAscending(builds);

        if(save) {
            List<Build> out = Lists.newArrayList();
            builds.forEach(build -> {
                context.setBuild(build);
                out.add(context.save(build));
            });
            return out;
        }else{
            return builds;
        }
    }

    public Build createBuild(Integer projectId, Job job){
        int buildNumber = job.getPipeline().getId();
        if (buildNumber <= context.getLatestBuildOnDB()) {
            return null;
        }

        Report report = gitLabReportService.getReport(projectId, job);

        Build build = Build.builder()
                .buildId(Integer.toString((buildNumber)))
                .displayName(job.getName() + "#" + job.getPipeline().getId())
                .number(buildNumber)
                .executedDate(job.getStartedAt() != null ? job.getStartedAt().getTime() : job.getCreatedAt().getTime())
                .status(StatusType.getStatus(job.getPipeline().getStatus().toString()))
                .url(job.getWebUrl().replaceFirst("-/jobs/\\d*", "pipelines/" + job.getPipeline().getId()))
                .artifacts(getArtifacts(job))
                .report(report)
                .enabled(true)
                .timestamp(DateUtils.now()).build();

        context.setReport(report);
        return build;
    }

    private List<Artifact> getArtifacts(Job job) {
        List<Artifact> artifacts = Lists.newArrayList();
        artifacts.add(Artifact.builder().name("output.log").url(job.getWebUrl()+"/raw").artifactType(ArtifactType.STANDARD_OUTPUT).build());
        return artifacts;
    }

}
