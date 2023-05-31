package com.clarolab.api.connection;

import com.clarolab.aaa.internal.ClientSecret;
import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.model.Connector;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.serviceDTO.ServiceAuthServiceDTO;
import com.clarolab.util.AuthenticationToken;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

public class PushServiceAPILargeTest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ServiceAuthServiceDTO serviceAuthServiceDTO;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void pushLargeFile() throws IOException {

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("JENKINS_LITH")
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080")
                .type(ConnectorType.JENKINS)
                .userName("rrincon")
                .userToken("94ab70cbeac2706bf5805ca852446a94")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        ServiceAuth serviceAuth = ServiceAuth.builder().connector(tConnector)
                .clientId("0wAclBz721BNRRLezy1pd4J1@G3ghQPD.i")
                .secretId("7SWOj6QDPqbu0LbxixBoUbGJPPdENugrQ3UP4m9jLt56LkfaoWqW3jymgULnRJKR")
                .build();
        serviceAuthService.save(serviceAuth);

        //Creation
        File file = FileUtils.getFile("src","test", "resources", "push", "push1.json");
        DataDTO dataDTO = new Gson().fromJson(Files.toString(file, Charsets.UTF_8), DataDTO.class);

        BuildDTO answerDTO = given()
                .body(dataDTO)
                .contentType(ContentType.JSON)
                .post(API_BUILD_URI + PUSH_PATH)
                .then()
                .statusCode(201)
                .extract()
                .as(BuildDTO.class);

        //Validation
        Assert.assertNotNull(answerDTO);
        Assert.assertEquals("Build should be converted to Push", answerDTO.getPopulateMode(), PopulateMode.PUSH.name());
    }

    public void authenticate() {
        ServiceAuthDTO credentials = serviceAuthServiceDTO.newServiceAuth(provider.getConnector().getId());
        ClientSecret clientSecret = new ClientSecret();
        clientSecret.setClientId(credentials.getClientID());
        clientSecret.setSecretId(credentials.getSecretID());
        authenticationToken =
                given()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .body(clientSecret)
                        .expect()
                        .statusCode(200)
                        .when()
                        .post(AUTH + TOKEN)
                        .body().as(AuthenticationToken.class);
        assertNotNull(authenticationToken);
        assertNotNull(authenticationToken.getAccessToken());
    }

}
