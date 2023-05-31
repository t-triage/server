/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.impl;

import com.clarolab.connectors.impl.circleCI.CircleCIApiClient;
import com.clarolab.connectors.services.ConnectorService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Builder
@Data
@Log
public class CircleCIConnectorService implements ConnectorService {

    private CircleCIApiClient circleCIApiClient;

    @Override
    public boolean getClientServiceStatus() {
        return this.getCircleCIApiClient().isRunning();
    }


    @Override
    public void cleanConnector() {
        this.getCircleCIApiClient().disconnect();
    }
}
