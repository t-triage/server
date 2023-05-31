/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.services;

import com.clarolab.client.JenkinsApiClient;
import com.clarolab.client.JenkinsJobClient;
import com.clarolab.client.JenkinsViewClient;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.entities.JenkinsContainer;
import com.clarolab.entities.JenkinsJob;
import com.clarolab.entities.JenkinsView;
import com.clarolab.http.utils.UrlUtils;
import com.clarolab.model.Container;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;


@Log
public class JenkinsViewService {

    private JenkinsViewClient jenkinsViewClient;
    private JenkinsJobClient jenkinsJobClient;

    @Builder
    private JenkinsViewService(JenkinsApiClient jenkinsApiClient){
        jenkinsViewClient = JenkinsViewClient.builder().jenkinsApiClient(jenkinsApiClient).build();
        jenkinsJobClient = JenkinsJobClient.builder().jenkinsApiClient(jenkinsApiClient).build();
    }

    public List<Container> getAllAsContainers() throws ContainerServiceException {
        List<Container> containers = Lists.newArrayList();

        try {
            //This add ListView, Dashboard, ListView under NestedView
            jenkinsViewClient.getAllViewsFromRoot().forEach(jenkinsView -> containers.add(createContainer(jenkinsView)));
            //TODO: Add support for multiJob and freeStyleProject w/downStream
        } catch (Exception e) {
            throw new ContainerServiceException(String.format("[getAllViews] : An error occurred trying to get containers"), e);
        }
        return containers;
    }

    public Container getAsContainer(String containerPathOrUrl) throws ContainerServiceException {

            String path;
            if(StringUtils.isURL(containerPathOrUrl))
                path = UrlUtils.getEndpoint(containerPathOrUrl).toString();
            else
                path = containerPathOrUrl;

        try {
            return createContainer(jenkinsViewClient.getView(path));
        } catch (Exception e) {
            log.log(Level.INFO, String.format("An error occurred trying to get View '%s' as container" , containerPathOrUrl), e);
        }

        try {
            //if not is view, can be multijob, folder, job with downstream
            return createContainer(jenkinsJobClient.getJob(containerPathOrUrl));
        } catch (Exception e) {
            log.log(Level.INFO, String.format("An error occurred trying to get MultiJob/Folder/Job '%s' as container" , containerPathOrUrl), e);
        }

        try {
            return createContainer(jenkinsViewClient.getContainer(containerPathOrUrl));
        } catch (Exception e) {
            log.log(Level.INFO, String.format("An error occurred trying to get '%s' as container" , containerPathOrUrl), e);
        }

        throw new ContainerServiceException(String.format("[getView] : An error occurred trying to get container %s" , containerPathOrUrl));
    }

    private boolean isView(String path){
        try {
            return jenkinsViewClient.getView(path).isListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path.matches("^/?view/") || !path.contains("/") && !(path.contains("/job/") || path.contains("job/"));
    }

    private Container createContainer(JenkinsView jenkinsView){
        return Container.builder()
                .name(jenkinsView.getName())
                .url(jenkinsView.getUrl())
                .description(jenkinsView.getDescription())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

    private Container createContainer(JenkinsJob jenkinsJob){
        return Container.builder()
                .name(jenkinsJob.getName())
                .url(jenkinsJob.getUrl())
                .description(jenkinsJob.getDescription())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

    private Container createContainer(JenkinsContainer jenkinsContainer){
        return Container.builder()
                .name(jenkinsContainer.getName())
                .url(jenkinsContainer.getUrl())
                .description(jenkinsContainer.getDescription())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

}
