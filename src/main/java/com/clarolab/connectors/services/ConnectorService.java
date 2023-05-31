/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services;

public interface ConnectorService {

    void cleanConnector();

    default boolean getClientServiceStatus() {
        return false;
    }
}
