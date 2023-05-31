/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.gitLab;

import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.http.client.HttpClient;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;

import java.util.List;
import java.util.Map;

@Log
public class GitLabApiClient {

    @Getter
    private HttpClient httpClient;
    private String url, userName, passwordOrToken;
    private GitLabApi gitLabApiClient;
    private GitLabProjectService gitLabProjectService;
    private GitLabJobService gitLabJobService;
    private GitLabBuildService gitLabBuildService;
    private ApplicationContextService context;

    @Builder
    public GitLabApiClient(String  url, String userName, String passwordOrToken, ApplicationContextService context){
        this.context = context;
        this.url = url;
        this.userName = userName;
        this.passwordOrToken = passwordOrToken;
        httpClient = HttpClient.builder().baseUrl(url).userName(userName).userPasswordOrToken(passwordOrToken).build();
        this.gitLabApiClient = new GitLabApi(url, passwordOrToken);
        //this.gitLabApiClient.enableRequestResponseLogging(Level.INFO);
        this.gitLabBuildService = GitLabBuildService.builder().context(context).gitLabApiClient(gitLabApiClient).gitLabReportService(GitLabReportService.builder().context(context).gitLabApiClient(gitLabApiClient).build()).build();
        this.gitLabJobService = GitLabJobService.builder().context(context).gitLabApiClient(this.gitLabApiClient).gitLabBuildService(gitLabBuildService).build();
        this.gitLabProjectService = GitLabProjectService.builder().context(context).gitLabApiClient(this.gitLabApiClient).gitLabJobService(this.gitLabJobService).gitLabBuildService(gitLabBuildService).build();
    }

    public boolean isRunning(){
        try {
            this.gitLabApiClient.getVersion();
            return true;
        } catch (GitLabApiException e) {
            new ConnectorServiceException(e);
            return false;
        }
    }

    public void disconnect(){
        this.clean();
    }

    public List<Project> getAllProjects() throws ConnectorServiceException {
        return this.gitLabProjectService.getAllProjects();
    }

    public Project getProject(String nameSpace, String projectName) throws ConnectorServiceException {
        return this.gitLabProjectService.getProject(nameSpace,projectName);
    }

    public Map<String, List<Job>> getJobs(Container container) throws ConnectorServiceException {
        return gitLabJobService.getMapJobs(Integer.parseInt(this.getProjectId(container)));
    }

    public Map<String, List<Job>> getJobs(int projectId) throws ConnectorServiceException {
        return gitLabJobService.getMapJobs(projectId);
    }

    public Map<String, List<Job>> getJobs(String nameSpace, String projectName) throws ConnectorServiceException {
        return gitLabJobService.getMapJobs(this.getProject(nameSpace, projectName).getId());
    }

    public List<Executor> getFromProject(Container container, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return this.getFromProject(Integer.parseInt(this.getProjectId(container)), maxBuildsToRetrieve);
    }

    public List<Executor> getFromProjectNotSave(Container container, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return this.getFromProjectNotSave(Integer.parseInt(this.getProjectId(container)), maxBuildsToRetrieve);
    }

    private List<Executor> getFromProject(int projectID, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return this.getExecutorList(Integer.valueOf(projectID), this.getJobs(projectID), maxBuildsToRetrieve);
    }

    private List<Executor> getFromProjectNotSave(int projectID, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return this.getExecutorListNotSave(Integer.valueOf(projectID), this.getJobs(projectID), maxBuildsToRetrieve);
    }

    public Job getJob(Container container, String jobName) throws ConnectorServiceException {
        return gitLabJobService.getJob(Integer.parseInt(this.getProjectId(container)), jobName);
    }

    public List<Build> getBuilds(Executor executor, int showPerPage, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return this.gitLabProjectService.getBuildsForProjectAndJob(Integer.parseInt(this.getProjectId(executor.getContainer())), executor.getName(), showPerPage, maxBuildsToRetrieve);
    }

    public Build getBuild(Executor executor, int buildNumber) throws ConnectorServiceException {
        return gitLabProjectService.getSpecificBuildForProjectAndJob(Integer.parseInt(this.getProjectId(executor.getContainer())), executor.getName(), buildNumber);
    }

    private List<Executor> getExecutorList(Integer projectID, Map<String, List<Job>> map, int maxBuildsToRetrieve){
        return getExecutorList(projectID, map, maxBuildsToRetrieve, true);
    }

    private List<Executor> getExecutorListNotSave(Integer projectID, Map<String, List<Job>> map, int maxBuildsToRetrieve){
        return getExecutorList(projectID, map, maxBuildsToRetrieve, false);
    }

    private List<Executor> getExecutorList(Integer projectID, Map<String, List<Job>> map, int maxBuildsToRetrieve, boolean save){
        List<Executor> executors = Lists.newArrayList();
        map.forEach((key, value) -> {
            Job moreRecent = gitLabJobService.getMoreRecentJob(value);
            log.info(String.format("Recovering data for Executor(name='%s')", moreRecent.getName()));
            Executor executor = Executor.builder()
                    .name(moreRecent.getName())
                    .description(moreRecent.toString())
                    .url(moreRecent.getWebUrl())
                    .enabled(true)
                    .timestamp(DateUtils.now()).build();
            context.setExecutorToContext(executor);
            //getBuildsForJobs performs a build save on db
            executor.add(gitLabBuildService.getBuildsForJobs(projectID, value, maxBuildsToRetrieve, save));
            executors.add(executor);
        });
        return executors;
    }

    private void clean(){
        this.url = null;
        this.userName = null;
        this.passwordOrToken = null;
        httpClient = null;
        this.gitLabApiClient = null;
    }


    private Map<String, String> getProjectParametersForApi(Container container){
        return this.getProjectParametersForApi(container.getHiddenData());
    }

    private Map<String, String> getProjectParametersForApi(String[] parameters){
        Map<String, String> map = Maps.newHashMap();
        map.put("projectNamespace", parameters[0]);
        map.put("projectPath", parameters[1]);
        map.put("projectId", parameters[2]);
        return map;
    }

    private Map<String, String> getProjectParametersForApi(String data){
        return this.getProjectParametersForApi(data.split("/"));
    }

    public String getProjectId(String data){
        return this.getProjectParametersForApi(data).get("projectId");
    }

    public String getProjectNameSpace(String data){
        return this.getProjectParametersForApi(data).get("projectNamespace");
    }

    public String getProjectPath(String data){
        return this.getProjectParametersForApi(data).get("projectPath");
    }

    public String getProjectId(Container container){
        return container.getHiddenData()[2];
    }

    public String getProjectNameSpace(Container container){
        return container.getHiddenData()[0];
    }

    public String getProjectPath(Container container){
        return container.getHiddenData()[1];
    }

}
