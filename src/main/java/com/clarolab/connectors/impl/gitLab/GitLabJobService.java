/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.gitLab;

import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.util.Constants;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Builder
public class GitLabJobService {

    private GitLabApi gitLabApiClient;
    private GitLabBuildService gitLabBuildService;
    private ApplicationContextService context;

    public Job getJob(int projectId, String jobName) throws ConnectorServiceException {
        int currentPage = 0;
        Map<String, List<Job>> map = this.getMapJobs(projectId, currentPage, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE);
        while (!map.isEmpty()){
            List<Job> jobs = map.get(jobName);
            if(CollectionUtils.isNotEmpty(jobs)) {
                //return the latest job to run
                return this.getMoreRecentJob(jobs);
            }
            currentPage += Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE;
            map = this.getMapJobs(projectId, currentPage, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE);
        }
        return null;
    }

    public Stream<Job> getStreamJobs(int projectId) throws ConnectorServiceException {
        List<Job> jobs = Lists.newArrayList();
        List<Job> gitLabJobs;
        int currentPage = 0;
        gitLabJobs = this.getStreamJobs(projectId, currentPage, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE).collect(Collectors.toList());
        while(CollectionUtils.isNotEmpty(gitLabJobs)) {
            jobs.addAll(gitLabJobs);
            currentPage += Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE;
            gitLabJobs = this.getStreamJobs(projectId, currentPage, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE).collect(Collectors.toList());
        }
        return jobs.stream();
    }

    public Map<String, List<Job>> getMapJobs(int projectId) throws ConnectorServiceException {
        return this.getStreamJobs(projectId).collect(Collectors.groupingBy(Job::getName));
    }

    public Map<String, List<Job>> getMapJobs(int projectId, int currentPage, int showPerPage) throws ConnectorServiceException {
        return this.getStreamJobs(projectId, currentPage, showPerPage).collect(Collectors.groupingBy(Job::getName));
    }

    public Stream<Job> getStreamJobs(int projectId, int currentPage, int showPerPage) throws ConnectorServiceException {
        try {
            return this.gitLabApiClient.getJobApi().getJobs(projectId, currentPage, showPerPage).stream();
        } catch (GitLabApiException e) {
            throw new ConnectorServiceException(e);
        }
    }

    public Job getMoreRecentJob(List<Job> jobs){
        //Comparator <Job> comparator = (j1, j2) -> Integer.compare(j1.getPipeline().getId(), j2.getPipeline().getId());
        //return jobs.stream().max(comparator).get();
        return jobs.stream().max(Comparator.comparing(j -> j.getPipeline().getId())).orElse(null);
    }

}
