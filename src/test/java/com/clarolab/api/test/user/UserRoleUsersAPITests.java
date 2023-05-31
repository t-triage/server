package com.clarolab.api.test.user;

import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;

import static com.clarolab.util.Constants.API_USER_URI;
import static com.clarolab.util.Constants.DELETE;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserRoleUsersAPITests extends UserServiceAPITests {

    @Before
    public void setUp() {
        regularUserSetUp();
    }

    @Override
    public void createUser() {
        Response createUserResponse = createUserSteps();
        createUserResponse.then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void updateDifferentUser() {
        Response updateUserResponse = updateEmailOfDifferentUserSteps(DataProvider.getEmail());
        updateUserResponse.then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void deleteUser() {
        //POST
        User user = createAnUserThruService(DataProvider.getUserAsAdmin());

        //DELETE
        given()
                .expect()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .when()
                .delete(API_USER_URI + DELETE + "/" + user.getId())
                .then().body("status", equalTo(HttpStatus.SC_FORBIDDEN));
    }

}
