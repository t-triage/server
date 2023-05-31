/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Connector;
import com.clarolab.model.Container;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ContainerService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContainerFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ContainerService containerService;



    @Ignore
    @Test
    public void testValidateContainerExists() {

        Connector connector = connectorService.save(
                Connector.builder()
                        .name("JENKINS_CLB")
                        .type(ConnectorType.JENKINS)
                        .url("http://dev.clarolab.com:12080")
                        .userName("rodrigo_rincon")
                        .userToken("afe066d6f6086a9dcb906d21e637f3d5")
                        .build()
        );

        Container container = containerService.save(
                Container.builder()
                        .connector(connector)
                        .name("Tests")
                        .url("http://dev.clarolab.com:12080/view/FolderToUseByJunits")
                        .populateMode(PopulateMode.PULL)
                        .build()
        );

        boolean valid = containerService.isValid(container.getId());
        Assert.assertTrue("The container is not valid", valid);
    }

    @Test
    public void testValidateContainerDoesntExists() {

        Connector connector = connectorService.save(
                Connector.builder()
                        .name("JENKINS_CLB")
                        .type(ConnectorType.JENKINS)
                        .url("http://dev.clarolab.com:12080")
                        .userName("rodrigo_rincon")
                        .userToken("afe066d6f6086a9dcb906d21e637f3d5")
                        .build()
        );

        Container container = containerService.save(
                Container.builder()
                        .connector(connector)
                        .name("INVALID")
                        .url("http://dev.clarolab.com:12080/view/INVALID/")
                        .populateMode(PopulateMode.PULL)
                        .build()
        );

        boolean valid = containerService.isValid(container.getId());
        Assert.assertFalse("The container shouldn't exist", valid);
    }


}
