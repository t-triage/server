/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api;

import com.clarolab.QAReportApplication;
import com.clarolab.model.Build;
import com.clarolab.model.ImageModel;
import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import com.clarolab.runner.category.ApiTestCategory;
import com.clarolab.service.*;
import com.clarolab.startup.License;
import com.clarolab.util.AuthenticationToken;
import com.clarolab.util.Credentials;
import com.clarolab.util.DateUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QAReportApplication.class)
@Category(ApiTestCategory.class)
public abstract class BaseAPITest {

    protected AuthenticationToken authenticationToken;
    protected static String username = System.getProperty("qe.user");
    protected String password = System.getProperty("qe.pass");

    @LocalServerPort
    private int port;

    @Autowired
    private LicenseService licenseService;

    protected static boolean initialized;

    @Before
    public void setUp() {
        /*
         If setup is not overrided in subclasses, will take admin credentials as default.
         These credentials were defined as properties and set in attribute definition.
          */
        if (!initialized)
            initialize();
    }

    protected void regularUserSetUp() {
        if (!initialized) {
            username = DataProvider.getEmail().toLowerCase();
            createRegularUser();
            initialize();
        }
    }

    protected void initializeRestAssured() {
        RestAssured.baseURI = "http://localhost";//System.getProperty("qe.server");
        RestAssured.port = port;
        RestAssured.basePath = ROOT_PATH;
        RestAssured.defaultParser = Parser.JSON;
    }

    protected void initialize() {
        initializeRestAssured();

        authenticate();

        //default settings
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer "+authenticationToken.getAccessToken())
                                                                   .addFilter(new RequestLoggingFilter())   //to log the complete request
                                                                   .addFilter(new ResponseLoggingFilter())  //to log the complete response
                                                                   .build();

        createDemoLicense();

        initialized = true;
    }

    private void validateOrCreateUser() {
        User user = userService.findByUsername(System.getProperty("qe.user"));
        if(user==null) {
            user = DataProvider.getUserAsAdmin();
            user.setUsername(System.getProperty("qe.user"));
            user.setRealname("Jon Snow");
            user.setPassword(userService.getEncryptedPassword(System.getProperty("qe.user")));

            user.setAvatar(ImageModel
                    .builder()
                    .enabled(true)
                    .name("DEMO IMAGE")
                    .timestamp(DateUtils.now())
                    .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                    .build());
            userService.save(user);
        }
    }

    protected void createRegularUser() {
        User user = DataProvider.getUserAsViewer();
        user.setId(0L);
        user.setUsername(username);
        user.setRealname(username);
        user.setPassword(userService.getEncryptedPassword(password));
        user.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());
        userService.save(user);
    }

    public void authenticate() {
        authenticationToken =
                doAuthentication()
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().as(AuthenticationToken.class);

        assertNotNull(authenticationToken);
        assertNotNull(authenticationToken.getAccessToken());
    }

    protected Response doAuthentication() {
        Credentials credentials = new Credentials();
        credentials.setEmail(username);
        credentials.setPassword(password);

        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(credentials)
                .log().all()
                .post(AUTH + LOGIN);
    }

    @Autowired
    private BuildService buildService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NoteService noteService;

    @Autowired
    protected ConnectorService connectorService;

    @Autowired
    protected ServiceAuthService serviceAuthService;



    protected User createAnUserThruService(User user){
        User save = userService.save(user);
        Assert.assertNotNull(save);
        return save;
    }


    protected void deleteThruService(Build build){
        buildService.delete(build.getId());
    }

    // Generic uri call and validation
    public ValidatableResponse testUri(String path) {
        return given()
                .get(path )
                .then()
                .time(lessThan(longTimeOut))
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    // Generic uri call returning an object
    public Object testUri(Class answerClass, String path) {
        Object answer = given()
                .get(path )
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(answerClass);
        Assert.assertNotNull(answer);

        return answer;
    }

    public User getLoggedUser() {
        return userService.findByUsername(System.getProperty("qe.user"));
    }

    @AfterClass
    public static void after() {
        initialized = false;
        username = System.getProperty("qe.user");
        RestAssured.reset();
    }

    public License createDemoLicense() {
        License license = null;
        if (licenseService.findAll().isEmpty()) {
            long date = DateUtils.now();

            long expStamp = DateUtils.daysFromToday(365);

            license = License.builder()
                    .creationTime(date)
                    .expirationTime(date + expStamp)
                    .expired(false)
                    .licenseCode("j2JxHD0xnPk0wOI3J37t1k4yiSD5epHTWDOi$XvvrvItfEEZWGyCyuiTcspJtSHI6PY7NJQFdli50nRTzIGnvVwXflA==")
                    .free(false)
                    .build();

            license = licenseService.save(license);
        }
        return license;
    }

}
