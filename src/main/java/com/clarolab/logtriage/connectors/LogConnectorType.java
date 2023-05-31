package com.clarolab.logtriage.connectors;

import com.clarolab.logtriage.connectors.impl.splunk.SplunkConnector;

import java.io.Serializable;

public enum LogConnectorType implements Serializable {
    SPLUNK(1) {
        @Override
        public ILogConnector getConnector(LogConnector connector) {
            return SplunkConnector.builder().connector(connector).build();
        }
    },
    ELASTIC(2) {
        @Override
        public ILogConnector getConnector(LogConnector connector) {
            return null;
        }
    };

    private final int connectorType;

    LogConnectorType(int connectorType) {
        this.connectorType = connectorType;
    }

    public int getLogConnectorType() {
        return this.connectorType;
    }

    public abstract ILogConnector getConnector(LogConnector connector);
}
