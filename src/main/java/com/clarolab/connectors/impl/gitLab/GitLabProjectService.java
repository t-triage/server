/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.gitLab;

import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.util.Constants;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.Project;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Builder
public class GitLabProjectService {

    private GitLabApi gitLabApiClient;
    private GitLabJobService gitLabJobService;
    private GitLabBuildService gitLabBuildService;
    private ApplicationContextService context;

    public List<Project> getAllProjects(int page, int showPerPage) throws ConnectorServiceException {
        try {
            return this.gitLabApiClient.getProjectApi().getProjects(page, showPerPage);
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
    }

    public List<Project> getAllProjects() throws ConnectorServiceException {
        List<Project> projects = Lists.newArrayList();
        int page = 1;
        try {
            List<Project> gitLabProjects = this.gitLabApiClient.getProjectApi().getProjects(page, 100);
            while(CollectionUtils.isNotEmpty(gitLabProjects)) {
                projects.addAll(gitLabProjects);
                page+=1;
                gitLabProjects = this.gitLabApiClient.getProjectApi().getProjects(page, 100);
            }
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
        return projects;
    }

    public Project getProject(String nameSpace, String projectName) throws ConnectorServiceException {
        int page = 1;
        try {
            List<Project> gitLabProjects = this.gitLabApiClient.getProjectApi().getProjects(page, 100);
            while(CollectionUtils.isNotEmpty(gitLabProjects)) {
                Project out = gitLabProjects.stream().filter(project -> project.getPath().equals(projectName.toLowerCase()) && project.getNamespace().getName().equals(nameSpace)).findFirst().orElse(null);
                if(out != null)
                    return out;
                page+=1;
                gitLabProjects = this.gitLabApiClient.getProjectApi().getProjects(page, 100);
            }
            return null;
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
    }

    public Stream<Integer> getAllPipelinesIdsForProject(int projectId) throws ConnectorServiceException {
        List<Integer> pipelinesIds = Lists.newArrayList();
        int page = 1;
        List<Integer> gitLabPipelinesIds = this.getPipelinesIdsForProject(projectId, page, 100).collect(Collectors.toList());
        while(CollectionUtils.isNotEmpty(gitLabPipelinesIds)) {
            pipelinesIds.addAll(gitLabPipelinesIds);
            page += 1;
            gitLabPipelinesIds = this.getPipelinesIdsForProject(projectId, page, 100).collect(Collectors.toList());
        }
        return pipelinesIds.stream();
    }

    public Stream<Integer> getPipelinesIdsForProject(int projectId, int page, int showPerPage) throws ConnectorServiceException {
        try {
            return this.gitLabApiClient.getPipelineApi().getPipelines(projectId, page, showPerPage).stream().map(Pipeline::getId);
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
    }

    public List<Build> getBuildsForProjectAndJob(int projectID, String jobName, int showPerPage, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return getBuildsForProjectAndJob(projectID, jobName, 0, showPerPage, maxBuildsToRetrieve);
    }

    public List<Build> getBuildsForProjectAndJob(int projectID, String jobName, int page, int showPerPage, int maxBuildsToRetrieve) throws ConnectorServiceException {
        List<Build> builds = Lists.newArrayList();
        try{
            List<Job> jobs = gitLabApiClient.getJobApi().getJobs(projectID, page, showPerPage);
            while(CollectionUtils.isNotEmpty(jobs) || builds.size() < maxBuildsToRetrieve){
                builds.addAll(this.gitLabBuildService.getBuildsForJobs(projectID, jobs.stream().filter(job -> job.getName().equals(jobName)).collect(Collectors.toList())));

                page += showPerPage;
                jobs = gitLabApiClient.getJobApi().getJobs(projectID, page, showPerPage);
            };

        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
        if(builds.size() > maxBuildsToRetrieve)
            return builds.stream().limit(maxBuildsToRetrieve).collect(Collectors.toList());

        return builds;
    }

    public Build getSpecificBuildForProjectAndJob(int projectID, String jobName, int buildNumber) throws ConnectorServiceException {
        try{
            int page = 0;
            Build build = null;
            List<Job> jobs = gitLabApiClient.getJobApi().getJobs(projectID, page, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE).stream().filter(job -> job.getName().equals(jobName)).collect(Collectors.toList());
            while(build == null && CollectionUtils.isNotEmpty(jobs)){

                Job j = jobs.stream().filter(job -> job.getPipeline().getId() == buildNumber).findFirst().orElse(null);

                if(j != null)
                    build = gitLabBuildService.createBuild(projectID, j);
                else{
                    page += Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE;
                    jobs = gitLabApiClient.getJobApi().getJobs(projectID, page, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE).stream().filter(job -> job.getName().equals(jobName)).collect(Collectors.toList());
                }
            };

            return build;
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
    }

}
