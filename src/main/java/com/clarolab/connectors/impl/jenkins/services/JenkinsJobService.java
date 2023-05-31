/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.services;

import com.clarolab.client.JenkinsApiClient;
import com.clarolab.client.JenkinsJobClient;
import com.clarolab.client.JenkinsViewClient;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsJob;
import com.clarolab.http.utils.UrlUtils;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class JenkinsJobService {


    private JenkinsJobClient jenkinsJobClient;
    private JenkinsViewClient jenkinsViewClient;

//    private JenkinsBuildService jenkinsBuildService;
    private ApplicationContextService context;

    @Builder
    private JenkinsJobService(JenkinsApiClient jenkinsApiClient, ApplicationContextService context){
        this.jenkinsJobClient = JenkinsJobClient.builder().jenkinsApiClient(jenkinsApiClient).build();
        jenkinsViewClient = JenkinsViewClient.builder().jenkinsApiClient(jenkinsApiClient).build();
        this.context = context;
//        this.jenkinsBuildService = JenkinsBuildService.builder().context(context).context(context).build();
    }

    public JenkinsJob getJob(String name) throws ExecutorServiceException {
        try {
            return jenkinsJobClient.getJob(name);
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[getJob] : An error occurred trying to get JenkinsJob(name=%s)", name), e);
        }
    }

    public boolean jobExists(String name) throws ExecutorServiceException {
        try {
            return jenkinsJobClient.isJobPresent(name);
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[jobExists] : An error occurred trying to get Executor(name=%s)", name), e);
        }
    }

    public boolean jobExists(String name, Container container) throws  ExecutorServiceException{
        String endpoint = UrlUtils.getEndpoint(container.getUrl()).toString();
        try {
            return jenkinsViewClient.getView(endpoint).isJobPresent(name);
        } catch (Exception e) {
            log.log(Level.INFO, String.format("There is no job: %s in View/Dashboard: %s", name, endpoint), e);
        }
        try {
            return jenkinsJobClient.getJob(endpoint).isJobPresent(name);
        } catch (Exception e) {
            log.log(Level.INFO, String.format("There is no job: %s in Folder/MultiJob/Job: %s", name, endpoint), e);
        }
        throw new ExecutorServiceException(String.format("[jobExists] : An error occurred trying to get Executor(name=%s) at Container(name=%s)", name, context.getContainer().getName()));
    }


    public List<Executor> getJobsOnContainerAsExecutors(Container container) throws  ExecutorServiceException{
        try {
            List<Executor> executors = Lists.newArrayList();
            List<JenkinsJob> jobs = jenkinsViewClient.getContainer(container.getUrl()).getAllJobs();
            jobs.forEach(job -> executors.add(createExecutor(job)));
            return executors;
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[getAllJobs] : An error occurred trying to get executors at Container(name=%s)", container.getName()), e);
        }
    }

    public List<Executor> getNewJobsOnContainerAsExecutors(Container container) throws ExecutorServiceException{
        try {
            List<Executor> newExecutors = Lists.newArrayList();
            List<JenkinsJob> jobs = jenkinsViewClient.getContainer(container.getUrl()).getAllJobs();
            List<JenkinsJob> newJobs = jobs.stream().filter(job -> LogicalCondition.NOT(context.getContainer().getExecutors().contains(Executor.builder().name(job.getName()).build()))).collect(Collectors.toList());
            newJobs.forEach(job -> newExecutors.add(createExecutor(job)));
            return newExecutors;
        }catch(Exception e){
            throw new ExecutorServiceException(String.format("[getNewJobsOnContainerAsExecutors] : An error occurred trying to get executors at Container(name=%s)]", container.getName()), e);
        }
    }

    //***************************************************************************************
    // ************************ Private declarations ****************************************
    //***************************************************************************************

    private String getInvokers(JenkinsJob jenkinsJob) {
        StringBuffer out = new StringBuffer();
        jenkinsJob.getUpstreamProjects().forEach(job -> out.append(job.getName()).append(","));
        return StringUtils.isEmpty(out.toString()) ? out.toString() : out.deleteCharAt(out.length()-1).toString();
    }

    private Executor createExecutor(JenkinsJob jenkinsJob) {
        Executor executor = Executor.builder()
                .name(jenkinsJob.getName())
                .description(jenkinsJob.getDescription())
                .url(jenkinsJob.getUrl())
                .callers(getInvokers(jenkinsJob))
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

//        List<Build> builds = Lists.newArrayList();
//        try {
//            builds.addAll(jenkinsBuildService.getBuilds(jenkinsJob, limit, -1));
//        }catch(Exception e){
//            log.warning(String.format("[createExecutor]: There was an error trying to get builds for JenkinsJob(name='%s', url='%s')", jenkinsJob.getDisplayName(), jenkinsJob.getUrl()));
//        }
//        executor.add(builds);
        return executor;
    }

}
