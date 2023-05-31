/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.impl.bamboo.BambooConnector;
import com.clarolab.connectors.impl.circleCI.CircleCIConnector;
import com.clarolab.connectors.impl.gitLab.GitLabConnector;
import com.clarolab.connectors.impl.jenkins.JenkinsConnector;
import com.clarolab.connectors.impl.qtest.QTestConnector;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Connector;

import java.io.Serializable;

public enum ConnectorType implements Serializable {

    JENKINS(1) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return JenkinsConnector.builder().url(connector.getUrl()).context(context).userName(connector.getUserName()).passwordOrToken(connector.getUserToken()).build();
        }
    },

    CIRCLECI(2) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return CircleCIConnector.builder().context(context).passwordOrToken(connector.getUserToken()).build();
        }
    },

    CRUISECONTROL(3) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return null;
        }
    },

    HUDSON(4) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return null;
        }
    },

    BAMBOO(5) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return BambooConnector.builder().url(connector.getUrl()).context(context).userName(connector.getUserName()).passwordOrToken(connector.getUserToken()).build();
        }
    },

    GITLAB(6){
      @Override
      public CIConnector getConnector(ApplicationContextService context, Connector connector) {
          return GitLabConnector.builder().context(context).url(connector.getUrl()).userName(connector.getUserName()).passwordOrToken(connector.getUserToken()).build();
      }
    },

    QTEST(7) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            return QTestConnector.builder().context(context).url(connector.getUrl()).passwordOrToken(connector.getUserToken()).build();
        }
    },

    UPLOAD(8) {
        @Override
        public CIConnector getConnector(ApplicationContextService context, Connector connector) {
            // this connector only allows upload from file
            return null;
        }
    };

    private final int connectorType;

    ConnectorType(int connectorType) {
        this.connectorType = connectorType;
    }

    public int getConnectorType() {
        return this.connectorType;
    }

    public abstract CIConnector getConnector(ApplicationContextService context, Connector connector);

}
