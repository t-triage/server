package com.clarolab.api.test.user;

import com.clarolab.dto.UserDTO;
import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import com.clarolab.util.Constants;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import static com.clarolab.util.Constants.API_USER_URI;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

public class AdminRoleUsersAPITests extends UserServiceAPITests {

    @Override
    public void createUser() {
        Response createUserResponse = createUserSteps();
        createUserResponse.then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(UserDTO.class);
    }

    @Override
    public void updateDifferentUser() {
        String email = DataProvider.getEmail();
        Response updateUserResponse = updateEmailOfDifferentUserSteps(email);

        UserDTO updated = updateUserResponse.as(UserDTO.class);


        Assert.assertEquals("Username can change in internal user", email, updated.getUsername());
        User dbUser = userService.findByUsername(email);
        Assert.assertNotNull("Username should have changed in the DB", dbUser);
        Assert.assertEquals("Realname should have been updated in DB", email, dbUser.getUsername());
    }

    @Override
    public void deleteUser() {
        //POST
        User user = createAnUserThruService(DataProvider.getUserAsAdmin());

        //DELETE
        given()
                .expect()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .when()
                .delete(API_USER_URI + "/delete/" + user.getId());

        //GET
        UserDTO result = expect()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(API_USER_URI + "/get/" + user.getId()).as(UserDTO.class);

        Assert.assertEquals(result.getEnabled(), false);

    }

    @Test
    public void deleteUserNotInList() {
        User user = DataProvider.getUserAsViewer();
        User as = createAnUserThruService(user);
        Assert.assertNotNull(as);

        //DELETE
        expect()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .when()
                .delete(API_USER_URI + "/delete/" + as.getId());

        //GET
        given()
                .get(API_USER_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .body("content", not(contains(as)));
    }

}
