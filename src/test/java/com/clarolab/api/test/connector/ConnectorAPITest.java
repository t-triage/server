/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test.connector;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.ConnectorDTO;
import com.clarolab.mapper.impl.ConnectorMapper;
import com.clarolab.model.Connector;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_CONNECTOR_URI;
import static com.clarolab.util.Constants.LIST_PATH;
import static io.restassured.RestAssured.given;

public abstract class ConnectorAPITest extends BaseAPITest {

    @Autowired
    protected UseCaseDataProvider provider;

    @Autowired
    protected ConnectorMapper connectorMapper;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    // Abstract tests are the basic ones that should be implemented for different roles

    @Test
    public abstract void testList();

    @Test
    public abstract void testCreate();

    @Test
    public abstract void testUpdate();

    @Test
    public abstract void testDelete();

    @Test
    public abstract void testGet();

    @Test
    public abstract void testAContainer();


    // Step methods

    protected Response requestContainersList() {
        return given().get(API_CONNECTOR_URI + LIST_PATH);
    }

    protected Response createConnectorSteps() {
        ConnectorDTO newDTO = connectorMapper.convertToDTO(DataProvider.getConnector());
        return given()
                .body(newDTO)
                .contentType(ContentType.JSON)
                .post(API_CONNECTOR_URI + Constants.CREATE_PATH);
    }

    protected Response updateContainerNameSteps(String newContainerName) {
        Connector entity = provider.getConnector();
        entity.setName(newContainerName);
        ConnectorDTO sendDTO = connectorMapper.convertToDTO(entity);
        return given()
                .body(sendDTO)
                .contentType(ContentType.JSON)
                .put(API_CONNECTOR_URI + Constants.UPDATE_PATH);
    }

    protected Response requestContainesOfAContainer() {
        provider.getContainer();
        String url = API_CONNECTOR_URI + "/" + provider.getConnector().getId() + "/containers";
        return given().get(url);
    }

}
