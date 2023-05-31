package com.clarolab.api.test.user;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.RestPageImpl;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.mapper.impl.UserPreferenceMapper;
import com.clarolab.model.Container;
import com.clarolab.model.User;
import com.clarolab.model.UserPreference;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.repository.UserRepository;
import com.clarolab.service.UserService;
import com.clarolab.util.Constants;
import com.jayway.jsonpath.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.clarolab.util.Constants.API_USER_PREFERENCES_URI;
import static com.clarolab.util.Constants.API_USER_URI;
import static com.clarolab.util.Constants.CREATE_PATH;
import static com.clarolab.util.Constants.DELETE;
import static com.clarolab.util.Constants.GET;
import static com.clarolab.util.Constants.LIST_PATH;
import static com.clarolab.util.Constants.ME;
import static com.clarolab.util.Constants.SEARCH;
import static com.clarolab.util.Constants.TERMS;
import static com.clarolab.util.Constants.UPDATE_PATH;
import static com.clarolab.util.Constants.longTimeOut;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

public abstract class UserServiceAPITests extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    protected UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Before
    public void userServiceTestSetUp() {
        provider.clear();
    }

    @Test
    public void getCurrentUser() {
        UserDTO response = given()
                .get(API_USER_URI + ME)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(UserDTO.class);

        response.getUsername().equals(username);
    }

    @Test
    public void testSearch1Leters() {

        String searchString = "K";
        String username = searchString + "testSearch2Leters";
        provider.setName(username);
        provider.getUser();

        ValidatableResponse response = given()
                .param("name", searchString)
                .get(API_USER_URI + SEARCH)
                .then();

        Assert.assertTrue(response.extract().body().jsonPath().getList("content").size() == 0);
    }

    @Test
    public void testSearch3Leters() {
        String searchString = "XJQ";
        String username = searchString + "testSearch3Leters";
        provider.setName(username);
        provider.getUser();

        ValidatableResponse response = given()
                .queryParam("name", searchString)
                .get(API_USER_URI + SEARCH)
                .then();

        String apiAnswer = response.extract().body().jsonPath().prettify();

        Assert.assertTrue(apiAnswer.contains(provider.getUser().getRealname()));

    }

    @Test
    public void testSearchDisabled() {
        String searchString = "D1S4";
        String username = searchString + "testSearchDisabled";
        provider.setName(username);
        provider.getUser();
        provider.getUser().setEnabled(false);
        provider.getUser().setRealname(username);
        userService.update(provider.getUser());

        ValidatableResponse response = given()
                .queryParam("name", searchString)
                .get(API_USER_URI + SEARCH)
                .then();

        Assert.assertTrue(response.extract().body().jsonPath().getList("content").size() == 0);

    }

    @Test
    public void testSearchUppercase() {
        String searchString = "UMQOP";
        String username = searchString.toLowerCase() + "testSearchUppercase";
        provider.setName(username);
        provider.getUser();

        ValidatableResponse response = given()
                .queryParam("name", searchString)
                .get(API_USER_URI + SEARCH)
                .then();

        String apiAnswer = response.extract().body().jsonPath().prettify();

        Assert.assertTrue(apiAnswer.contains(provider.getUser().getRealname()));

    }

    @Test
    public void testSearchLastName() {
        String searchString = "Lovelace";
        String username = "Ada " + searchString;
        provider.setName(username);
        provider.getUser();
        provider.getUser().setRealname(username);
        userService.update(provider.getUser());

        TypeRef<RestPageImpl<UserDTO>> responseType = new TypeRef<RestPageImpl<UserDTO>>() {};
        RestPageImpl<UserDTO> usersResult = given()
                .queryParam("name", searchString)
                .get(API_USER_URI + SEARCH)
                .as(responseType.getType());

        Assert.assertNotNull(usersResult.getContent());
        List<UserDTO> answer = usersResult.getContent();

        Assert.assertNotEquals(0, answer.size());

        boolean found = false;
        for (UserDTO user : answer) {
            if (user.getRealname().contains(searchString)) {
                found = true;
            }
        }
        Assert.assertTrue(found);

    }

    @Test
    public void testUserListTimeOut() {
        //GET
        given()
                .get(API_USER_URI + Constants.LIST_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(longTimeOut));
    }

     @Test
     public void listUsers() {
        Response response = given().get(API_USER_URI + Constants.LIST_PATH);
        response.then().statusCode(HttpStatus.SC_OK);
        Assert.assertFalse("Response did nor retrieve users", response.jsonPath().getList("content", UserDTO.class).isEmpty());
     }

    @Test
    public void getOtherUserById() {
        User current = userService.findByUsername(username);
        assert current != null;

        Optional<User> user = userService.findAll().stream().filter(u -> u.getId().intValue() != current.getId().intValue()).findFirst();

        if (!user.isPresent())
            throw new AssertionError("There was not found any user to test against");

        int id = user.get().getId().intValue();

        UserDTO userDTO = given()
                .get(API_USER_URI + GET + "/" + id)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(UserDTO.class);

        user.get().getUsername().equals(userDTO.getUsername());
    }

    @Test
    public void getOwnUserById() {
        User user = userService.findByUsername(username);

        given()
                .get(API_USER_URI + GET + "/" + user.getId().intValue())
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(UserDTO.class);
    }

    //This is not longer valid since the services doesnt throw the ErrorInfo Anymore
    @Ignore
    public void testUserCreateDuplicatedUsernames() {
        UserDTO user1 = userMapper.convertToDTO(DataProvider.getUserAsAdmin());
        given()
                .body(user1)
                .when()
                .contentType(ContentType.JSON)
                .post(API_USER_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_OK);

        UserDTO user2 = userMapper.convertToDTO(DataProvider.getUserAsAdmin());
        user2.setUsername(user1.getUsername());

        ErrorInfo result = given()
                .body(user2)
                .when()
                .contentType(ContentType.JSON)
                .post(API_USER_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract().as(ErrorInfo.class);

        Assert.assertEquals(result.getCode(), HttpStatus.SC_UNAUTHORIZED);
        Assert.assertEquals(result.getError(), "Unauthorized");
    }

    @Test
    public void getTyC() {
        Response response = given().header("accept", TEXT_HTML_VALUE)
                .when()
                .get(API_USER_URI + TERMS);
        response.then().statusCode(HttpStatus.SC_OK)
                .header("Content-Type", startsWith(TEXT_HTML_VALUE));
    }

    @Test
    @Ignore("Not implemented from backend side")
    public void createPreference() {
        Optional<User> found = userRepository.findByUsername(username);
        Container container = provider.getContainer();
        if (!found.isPresent() || container == null)
            throw new AssertionError(String.format("User with name '%s' to use in test was not found in database and/or unable to build container to work with."));

        UserDTO user = userMapper.convertToDTO(found.get());

        UserPreferenceDTO userPreferenceDTO = DataProvider.getNewPreference(user);

        UserPreferenceDTO response = given()
                .when().contentType(ContentType.JSON).body(userPreferenceDTO)
                .post(API_USER_PREFERENCES_URI + CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(UserPreferenceDTO.class);

        assert response.equals(userPreferenceDTO);

    }

    @Test
    @Ignore("Not implemented from backend side")
    public void listPreferences() {
        Container container = provider.getContainer();
        provider.newPreferenceGivenUser(username);

        Response response = given()
                .queryParam("unpaged", true)
                .get(API_USER_PREFERENCES_URI + LIST_PATH);

        response.then().statusCode(HttpStatus.SC_OK);
        List<UserPreferenceDTO> preferences = response.jsonPath().getList("content", UserPreferenceDTO.class);
        // No empty preferences in result set
        assert !preferences.isEmpty();
        // The expected container is present in the result set
        assert preferences.stream().filter(f -> container.getId().equals(new Long(f.getCurrentContainer()))).findFirst().isPresent();
    }

    @Test
    @Ignore("Not implemented from backend side")
    public void getPreference() {
        provider.getContainer();
        UserPreference userPreference = provider.newPreferenceGivenUser(username);
        UserPreferenceDTO expected = userPreferenceMapper.convertToDTO(userPreference);

        Response response = given()
                .get(API_USER_PREFERENCES_URI + GET + "/" + userPreference.getId());

        response.then().statusCode(HttpStatus.SC_OK);
        UserPreferenceDTO userPreferenceDTO = response.then().extract().as(UserPreferenceDTO.class);
        assert userPreferenceDTO.equals(expected);
    }

    @Test
    @Ignore("Not implemented from backend side")
    public void updatePreference() {
        Optional<User> found = userRepository.findByUsername(username);

        if (!found.isPresent())
            throw new AssertionError(String.format("User '%s' to use in test was not found."));
        UserDTO userDTO = userMapper.convertToDTO(found.get());

        provider.getContainer();
        UserPreference userPreference = provider.newPreferenceGivenUser(username);

        UserPreferenceDTO userPreferenceDTO = userPreferenceMapper.convertToDTO(userPreference);
        userPreferenceDTO.setEnabled(false);
        userPreferenceDTO.setRowPerPage(2);
        userPreferenceDTO.setUser(userDTO);

        given()
                .when().contentType(ContentType.JSON).body(userPreferenceDTO)
                .put(API_USER_PREFERENCES_URI + UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .body("enabled", is(false))
                .body("updated", not(nullValue()))
                .body("rowPerPage", is(2))
                .body("updated", not(equalTo(userPreference.getUpdated())));
    }

    @Test
    @Ignore("Not implemented from backend side")
    public void deletePreference() {
        provider.getContainer();
        UserPreference userPreference = provider.newPreferenceGivenUser(username);

        Response response = given()
                .delete(API_USER_PREFERENCES_URI + DELETE + "/" + userPreference.getId());

        response.then().statusCode(HttpStatus.SC_ACCEPTED);

        String currentId = response.getBody().print();

        // Asserts the returned id is the expected one
        assert currentId.equals(String.valueOf(userPreference.getId().intValue()));
    }

    @Test
    @Ignore("Not implemented from backend side")
    public void deleteUnexistingPreference() {
        given()
                .delete(API_USER_PREFERENCES_URI + DELETE + "/" + RandomStringUtils.randomNumeric(2))
                .then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testUserUpdatesItself() {
        User current = userService.findByUsername(username);
        Assert.assertNotNull(current);

        UserDTO userDTO = userMapper.convertToDTO(current);

        String newText = DataProvider.getRandomName("NewName");
        userDTO.setRealname(newText);

        //PUT
        UserDTO updated = given()
                .body(userDTO)
                .when()
                .contentType(ContentType.JSON)
                .put(API_USER_URI + Constants.UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(UserDTO.class);

        //GET
        UserDTO result = expect()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(API_USER_URI + "/get/" + current.getId()).as(UserDTO.class);

        Assert.assertEquals("Realname should have been updated", newText, updated.getRealname());

        User dbUser = userService.findByUsername(current.getUsername());

        Assert.assertNotNull("User should have been created", dbUser);
        Assert.assertEquals("Realname should have been updated in DB", newText, dbUser.getRealname());
        Assert.assertEquals("Realname should have been updated in DTO", newText, result.getRealname());
    }

    @Test
    public abstract void createUser();

    @Test
    public abstract void updateDifferentUser();

    @Test
    public abstract void deleteUser();

    protected Response createUserSteps() {
        User userAsAdmin = DataProvider.getUserAsAdmin();
        userAsAdmin.setId(0L);
        UserDTO user = userMapper.convertToDTO(userAsAdmin);
        return given()
                .body(user)
                .when()
                .contentType(ContentType.JSON)
                .post(API_USER_URI + Constants.CREATE_PATH);
    }

    protected Response updateEmailOfDifferentUserSteps(String newEmail) {
        User user = DataProvider.getUserAsAdmin();

        user = createAnUserThruService(user);
        Assert.assertNotNull(user);

        user.setUsername(newEmail);

        //PUT
        return given()
                .body(userMapper.convertToDTO(user))
                .when()
                .contentType(ContentType.JSON)
                .put(API_USER_URI + Constants.UPDATE_PATH);
    }

}
