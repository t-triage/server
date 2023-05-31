/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI;

import com.clarolab.connectors.impl.circleCI.model.entities.*;
import com.clarolab.connectors.impl.circleCI.model.gson.deserializer.CircleCIJobDeserializer;
import com.clarolab.connectors.impl.circleCI.model.gson.deserializer.CircleCIProjectDeserializer;
import com.clarolab.connectors.impl.circleCI.report.CircleCiReport;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.http.client.HttpClient;
import com.clarolab.model.Report;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class CircleCIApiClient {

    @Getter
    private HttpClient httpClient;
    private String url, userName, passwordOrToken;
    private Map<String, GsonBuilder> gsonBuilder = Maps.newHashMap();
    private ApplicationContextService context;

    @Builder
    public CircleCIApiClient(String  url, String userName, String passwordOrToken, ApplicationContextService context){
        this.context = context;
        this.url = Strings.isNullOrEmpty(url) ? CircleCIApiEndpoints.CIRCLE_API_BASE_ENDPOINT : url;
        this.userName = userName;
        this.passwordOrToken = passwordOrToken;
        httpClient = HttpClient.builder().baseUrl(url).userName(this.passwordOrToken).userPasswordOrToken(this.userName).build();
        gsonBuilder.put("GsonBuilderFullJobs", new GsonBuilder().registerTypeAdapter(CircleCIEntity.class, new CircleCIProjectDeserializer()).registerTypeAdapter(CircleCIJobEntity.class, new CircleCIJobDeserializer(httpClient)));
        gsonBuilder.put("GsonBuilderPlainJobs", new GsonBuilder().registerTypeAdapter(CircleCIEntity.class, new CircleCIProjectDeserializer()).registerTypeAdapter(CircleCIJobEntity.class, new CircleCIJobDeserializer(httpClient, true)));
    }

    public boolean isRunning(){
        if(httpClient == null){
            log.info("Http Client was cleaned. Can not get the real status from Circle CI.");
            return false;
        }
        try {
            httpClient.prepareRequest(CircleCIApiEndpoints.ME_ENDPOINT);
            httpClient.performRequest();
            httpClient.validateResponse();
            httpClient.clean();
            return true;
        } catch (Exception e) {
            log.severe("Failed to clean http client: " + e.getMessage());
            log.severe(e.getMessage());
            return false;
        }
    }

    public void disconnect(){
        this.clean();
    }

    public List<CircleCIProjectEntity> getAllProjects() throws ConnectorServiceException {
        try {
            return  httpClient.get(gsonBuilder.get("GsonBuilderFullJobs"), CircleCIApiEndpoints.PROJECTS_ENDPOINT, CircleCIEntity.class).getCircleCIProjectDataList();
        } catch (Exception e) {
            throw new ConnectorServiceException(e);
        }
    }

    public Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String vcsType, String vcsUserName, String projectName, int offsetLimit, int startFrom) throws ConnectorServiceException {
        return this.getAllJobs(vcsType, vcsUserName, projectName, offsetLimit, startFrom, false);
    }
    public Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String vcsType, String vcsUserName, String projectName, int page, int showPerPage, boolean plainJobs) throws ConnectorServiceException {
        try {
            GsonBuilder builder = plainJobs ? gsonBuilder.get("GsonBuilderPlainJobs") : gsonBuilder.get("GsonBuilderFullJobs");
            List<CircleCIJobWithDetailsEntity> list = httpClient.get(builder, String.format(CircleCIApiEndpoints.BUILDS_FOR_PROJECT_ENDPOINT_WITH_RANGE, vcsType, vcsUserName, projectName, page, showPerPage), CircleCIJobEntity.class)
                        .getCircleCIJobWithDetailsEntityList();
            Stream<CircleCIJobWithDetailsEntity> stream = list.stream().filter(job -> job.isValidJob());
            return stream.collect(Collectors.groupingBy(CircleCIJobWithDetailsEntity::getJobName));
        } catch (Exception e) {
            throw new ConnectorServiceException(e, "[ Error: getAllJobs ] | " + String.format(CircleCIApiEndpoints.BUILDS_FOR_PROJECT_ENDPOINT_WITH_RANGE, vcsType, vcsUserName, projectName, page, showPerPage));
        }
    }

    public Report generateReportFromArtifact(CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithReport, CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion) throws ReportServiceException {
        try {
            return circleCIProjectBuildArtifactElementEntityWithReport == null ?
                    null :
                    CircleCiReport.builder().context(context).build().getReport(circleCIProjectBuildArtifactElementEntityWithReport, circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion, httpClient);
        } catch (ConnectorServiceException e) {
            throw ReportServiceException.builder().message(String.format("[generateReportFromArtifact] : An error occurred trying to get file report on %s", circleCIProjectBuildArtifactElementEntityWithReport.getFilePathRequest())).exception(e).build();
        }
    }

    private void clean(){
        this.url = null;
        this.userName = null;
        this.passwordOrToken = null;
        this.httpClient = null;
        this.gsonBuilder = null;
    }

}
