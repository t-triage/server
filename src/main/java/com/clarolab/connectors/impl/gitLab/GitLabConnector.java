/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.gitLab;


import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.http.utils.UrlUtils;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.gitlab4j.api.models.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Transactional
public class GitLabConnector implements CIConnector {

    private GitLabApiClient gitLabApiClient;
    private ApplicationContextService context;

    @Builder
    private GitLabConnector(String url, String userName, String passwordOrToken, ApplicationContextService context){
        this.context = context;
        gitLabApiClient = GitLabApiClient.builder().context(context).url(url).userName(userName).passwordOrToken(passwordOrToken).build();
    }

    @Override
    public CIConnector connect() {
        if(gitLabApiClient.isRunning())
            return this;
        return null;
    }

    @Override
    public void disconnect() {
        this.gitLabApiClient.disconnect();
    }

    @Override
    public boolean isConnected() {
        try {
            return gitLabApiClient.isRunning();
        }catch (NullPointerException e){
            return false;
        }
    }

    @Override
    public Container containerExists(Container container) throws ContainerServiceException {
        return getContainer(container.getUrl());
    }

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException {
        List<Container> containerList = Lists.newArrayList();
        try {
            gitLabApiClient.getAllProjects().forEach(project -> {
                containerList.add(Container.builder()
                                            .name(project.getName())
                                            .hiddenData(project.getPathWithNamespace()+"/"+project.getId())
                                            .description(project.getDescription())
                                            .url(project.getWebUrl())
                                            .enabled(true)
                                            .timestamp(DateUtils.now()).build());
            });
            return containerList;
        } catch (ConnectorServiceException e) {
            throw new ContainerServiceException(String.format("[getAllContainers] : An error occurred trying to get containers"), e);
        }
    }

    @Override
    //containerName should be the hiddenData
    public Container getContainer(String containerNameOrUrl) throws ContainerServiceException {
        String nameSpace, projectPath;

        if(StringUtils.isURL(containerNameOrUrl)){
            String tailURL = UrlUtils.getEndpoint(containerNameOrUrl).toString();
            tailURL = StringUtils.removeFirstWithCondition(tailURL, "/");
            tailURL = StringUtils.removeLastWithCondition(tailURL, "/");
            String[] parameters = tailURL.split("/");
            if(parameters.length != 2)
                throw new ContainerServiceException(String.format("[getContainer] : There are more than two parameters after url. It should be like [url]/spaceName/project"));
            nameSpace = parameters[0];
            projectPath = parameters[1];
        }else {
            nameSpace = gitLabApiClient.getProjectNameSpace(containerNameOrUrl);
            projectPath = gitLabApiClient.getProjectPath(containerNameOrUrl);
        }

        try {
            Project project = gitLabApiClient.getProject(nameSpace, projectPath);
            if (project != null) {
                return Container.builder()
                        .name(project.getName())
                        .hiddenData(project.getPathWithNamespace() + "/" + project.getId())
                        .description(project.getDescription())
                        .url(project.getWebUrl())
                        .enabled(true)
                        .timestamp(DateUtils.now()).build();
            }
            return null;
        } catch (ConnectorServiceException e) {
            throw new ContainerServiceException(String.format("[getContainer] : An error occurred trying to get container %s", gitLabApiClient.getProjectPath(containerNameOrUrl)), e);
        }
    }

    @Override
    public boolean isExecutorAlive(Executor executor) throws ExecutorServiceException {
        return this.isExecutorAlive(executor, executor.getContainer());
    }

    @Override
    public boolean isExecutorAlive(Executor executor, Container container) throws ExecutorServiceException {
        try {
            return gitLabApiClient.getJob(container, executor.getName()) != null;
        } catch (ConnectorServiceException e) {
            throw new ExecutorServiceException(String.format("[isExecutorAlive] : An error occurred trying to get Executor(name=%s) at Container(name=%s)", executor.getName(), container.getName()), e);
        }
    }

    @Override
    public List<Executor> getAllExecutors(Container container) throws ExecutorServiceException {
        return this.getAllExecutors(container, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE);
    }

    @Override
    public List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        try {
            List<Executor> executors = gitLabApiClient.getFromProject(container, maxBuildsToRetrieve);
            return saveOrUpdate(container, executors);

        } catch (ConnectorServiceException e) {
            throw new ExecutorServiceException(String.format("[getAllExecutors] : An error occurred trying to get executors for Container(name=%s)", container.getName()));
        }
    }

    @Override
    public List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        try {
            List<Executor> news = gitLabApiClient.getFromProjectNotSave(container, maxBuildsToRetrieve).stream()
                    .filter(executor -> LogicalCondition.NOT(container.getExecutors().contains(Executor.builder().name(executor.getName()).build())))
                    .collect(Collectors.toList());
            return saveOrUpdate(container, news);
        } catch (ConnectorServiceException e) {
            throw new ExecutorServiceException(String.format("[checkForNewExecutors] : An error occurred trying to get executors for Container(name=%s)", container.getName()));
        }
    }

    @Override
    public List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        return null;
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException {
        return this.getExecutorBuilds(executor, Constants.DEFAULT_MAX_BUILDS_TO_PROCESS);
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        try {
            return gitLabApiClient.getBuilds(executor, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE, maxBuildsToRetrieve);
        } catch (ConnectorServiceException e) {
            throw new BuildServiceException("[getExecutorBuilds] : An error occurred trying to get builds for " + executor.getName());
        }
    }

    @Override
    public int getExecutorLatestBuild(Executor executor) throws BuildServiceException {
        try {
            return gitLabApiClient.getJob(executor.getContainer(), executor.getName()).getPipeline().getId();
        } catch (ConnectorServiceException e) {
            throw new BuildServiceException(String.format("[getLatestBuildForJob] : An error occurred trying to get latest build for executor %s]", executor.getName()), e);
        }
    }

    @Override
    public Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException {
        try {
            return gitLabApiClient.getBuild(executor, buildNumber);
        } catch (ConnectorServiceException e) {
            throw new BuildServiceException(String.format("[getExecutorBuild] : An error occurred trying to get build %d for executor %s]",buildNumber, executor.getName()), e);
        }
    }

    public ApplicationContextService getContext() {
        return context;
    }

    private List<Executor> saveOrUpdate(Container container, List<Executor> executors){
        List<Executor> answer = Lists.newArrayList();
        for (Executor executor : executors) {
            if (executor.getContainer() == null) {
                container.add(executor);
                answer.add(context.getExecutorService().save(executor));
            } else {
                answer.add(context.getExecutorService().update(executor));
            }
        }
        return answer;
    }
}
