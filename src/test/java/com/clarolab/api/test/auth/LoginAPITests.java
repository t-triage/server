package com.clarolab.api.test.auth;

import com.clarolab.api.BaseAPITest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public abstract class LoginAPITests extends BaseAPITest {

    @Before
    public void init() {
        initializeRestAssured();
    }

    @Test
    public void doLogin() {
        Response response = doAuthentication();
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "accessToken", not(empty()),
                        "tokenType", equalTo("Bearer")
                );
    }

}
