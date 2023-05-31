/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.impl;

import com.clarolab.connectors.impl.gitLab.GitLabApiClient;
import com.clarolab.connectors.services.ConnectorService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Builder
@Data
@Log
public class GitLabConnectorService implements ConnectorService {

    private GitLabApiClient gitLabApiClient;

    @Override
    public boolean getClientServiceStatus() { return this.getGitLabApiClient().isRunning(); }


    @Override
    public void cleanConnector() {
        this.getGitLabApiClient().disconnect();
    }
}
