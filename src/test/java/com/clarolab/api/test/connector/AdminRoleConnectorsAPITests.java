package com.clarolab.api.test.connector;

import com.clarolab.dto.ConnectorDTO;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.model.Connector;
import com.clarolab.populate.DataProvider;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.expect;

public class AdminRoleConnectorsAPITests extends ConnectorAPITest {

    @Override
    public void testList() {
        testUri(API_CONNECTOR_URI + LIST_PATH);
    }

    @Test
    public void testConnectorContainerList() {
        provider.getContainer();
        testUri(API_CONNECTOR_URI + LIST_PATH);
    }

    @Override
    public void testCreate() {
        createConnectorSteps()
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(ConnectorDTO.class);
    }

    @Override
    public void testUpdate() {
        String newName = DataProvider.getRandomName("NewName");
        ConnectorDTO dto = updateContainerNameSteps(newName)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(ConnectorDTO.class);

        Assert.assertEquals(dto.getName(), newName);
    }

    @Override
    public void testDelete() {
        Connector entity = provider.getConnector();
        expect()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .when()
                .delete(API_CONNECTOR_URI + DELETE + "/" + entity.getId());
    }

    @Override
    public void testGet() {
        testUri(ConnectorDTO.class, API_CONNECTOR_URI + GET + "/" + provider.getConnector().getId());
    }

    @Override
    public void testAContainer() {
        TypeRef<List<ContainerDTO>> listType = new TypeRef<List<ContainerDTO>>() {
        };

        List<ContainerDTO> containers = requestContainesOfAContainer()
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(listType.getType());
    }
}
