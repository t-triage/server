package com.clarolab.api.test.auth;

import com.clarolab.aaa.internal.RegistrationRequest;
import com.clarolab.aaa.internal.RegistrationResponse;
import com.clarolab.api.BaseAPITest;
import com.clarolab.populate.DataProvider;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static com.clarolab.util.Constants.AUTH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class SignupAPITests extends BaseAPITest {

    @Before
    public void setUp() {
        initializeRestAssured();
    }

    @Test
    public void doSignup() {
        RegistrationRequest registrationRequest = DataProvider.getRegistration();
        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(AUTH + Constants.SIGNUP);

        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(
                        "success", is(true),
                        "message", equalTo("User registered successfully.")
                );
    }

}
