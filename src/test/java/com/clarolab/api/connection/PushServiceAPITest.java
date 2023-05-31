package com.clarolab.api.connection;

import com.clarolab.aaa.internal.ClientSecret;
import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ContainerService;
import com.clarolab.service.ExecutorService;
import com.clarolab.serviceDTO.ServiceAuthServiceDTO;
import com.clarolab.util.AuthenticationToken;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

public class PushServiceAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ServiceAuthServiceDTO serviceAuthServiceDTO;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ContainerService containerService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void pushBasicPass() {
        //Creation
        DataDTO dataDTO = provider.getDataDTO(StatusType.PASS);

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

    /*@Test
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
    }*/

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

    @Test
    public void pushText() {
        provider.getContainer().setName("Automation_Bobcat");
        provider.getExecutor().setName("QA_LIA_RESPONSIVE_GROUP_HUBS");
        // provider.getExecutor().setName("QA_LIA_RESPONSIVE_GROUP_HUBS");
        provider.getExecutor().setUrl("https://jenkins.dev.lithium.com/job/QA_LIA_RESPONSIVE_GROUP_HUBS/");
        executorService.update(provider.getExecutor());
        containerService.update(provider.getContainer());
        String data = "{" +
                "    \"viewName\": \"Automation_Bobcat\"," +
                "    \"jobId\": 0," +
                "    \"jobName\": \"QA_LIA_RESPONSIVE_GROUP_HUBS\"," +
                "    \"jobUrl\": \"https://jenkins.dev.lithium.com/job/QA_LIA_RESPONSIVE_GROUP_HUBS/\"," +
                "    \"buildNumber\": 103," +
                "    \"buildStatus\": \"UNKNOWN\"," +
                "    \"buildUrl\": \"https://jenkins.dev.lithium.com/job/QA_LIA_RESPONSIVE_GROUP_HUBS/103/\"," +
                "    \"artifacts\": [" +
                "        {" +
                "            \"fileName\": \"output.log\"," +
                "            \"content\": \"https://jenkins.dev.lithium.com/job/QA_LIA_RESPONSIVE_GROUP_HUBS/103/consoleFull\"," +
                "            \"fileType\": \"log\"," +
                "            \"url\": \"https://jenkins.dev.lithium.com/job/QA_LIA_RESPONSIVE_GROUP_HUBS/103/consoleFull\"" +
                "        }" +
                "    ]," +
                "    \"timestamp\": 1596662354944," +
                "    \"triggerName\": \"Test\"," +
                "    \"clientId\": \"nZdwI11R0TaN2yROwF04xRwC@YxfDdSH.i\"" +
                "}";

        ExtractableResponse answer = given()
                .body(data)
                .contentType(ContentType.JSON)
                .post(API_BUILD_URI + PUSH_PATH)
                .then()
                .statusCode(201)
                .extract();

        //Validation
        Assert.assertNotNull(answer);
    }

}
