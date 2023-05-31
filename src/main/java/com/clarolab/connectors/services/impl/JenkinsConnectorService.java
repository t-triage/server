/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.impl;

import com.clarolab.client.JenkinsApiClient;
import com.clarolab.client.JenkinsServerClient;
import com.clarolab.connectors.services.ConnectorService;
import com.clarolab.http.client.HttpClient;
import lombok.Builder;

public class JenkinsConnectorService implements ConnectorService {

    private JenkinsApiClient apiClient;
    private JenkinsServerClient serverClient;

    @Builder
    public JenkinsConnectorService(String url, String userName, String passwordOrToken) {
        apiClient = JenkinsApiClient.builder().baseUrl(url).userName(userName).passwordOrToken(passwordOrToken).build();
        serverClient = JenkinsServerClient.builder().jenkinsApiClient(apiClient).build();
    }

    @Override
    public boolean getClientServiceStatus() {
        return serverClient.isServerRunning();
    }

    @Override
    public void cleanConnector() {
        apiClient.getHttpClient().close();
    }

    public JenkinsApiClient getJenkinsApiClient() {
        return this.apiClient;
    }

    public HttpClient getHttpClient(){
        return apiClient.getHttpClient();
    }

}
