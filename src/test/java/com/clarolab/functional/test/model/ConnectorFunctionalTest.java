/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Connector;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.service.ConnectorService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private ConnectorService connectorService;

    @Ignore
    @Test
    public void testValidateConnectorExists() {

        Connector connector = connectorService.save(
                Connector.builder()
                        .name("JENKINS_CLB")
                        .type(ConnectorType.JENKINS)
                        .url("http://dev.clarolab.com:12080")
                        .userName("rodrigo_rincon")
                        .userToken("afe066d6f6086a9dcb906d21e637f3d5")
                        .build()
        );

        boolean valid = connectorService.isValid(connector.getId());
        Assert.assertTrue("The connector is not valid", valid);
    }

    @Test
    public void testValidateConnectorDoesntExists() {

        Connector connector = connectorService.save(
                Connector.builder()
                        .name("JENKINS_CLB")
                        .type(ConnectorType.JENKINS)
                        .url("http://dev.clarolab.net")
                        .userName("rodrigo_rincon")
                        .userToken("afe066d6f6086a9dcb906d21e637f3d5")
                        .build()
        );

        boolean valid = connectorService.isValid(connector.getId());
        Assert.assertFalse("The connector shouldn't exist", valid);
    }
}
