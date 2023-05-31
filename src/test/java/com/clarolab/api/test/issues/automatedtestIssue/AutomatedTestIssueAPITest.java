package com.clarolab.api.test.issues.automatedtestIssue;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.FilterParam;
import com.clarolab.api.util.RestPageImpl;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.mapper.impl.AutomatedTestIssueMapper;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.User;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.repository.AutomatedTestIssueRepository;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.TestExecutionService;
import com.clarolab.service.TestTriageService;
import com.clarolab.service.UserService;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clarolab.util.Constants.API_AUTOMATED_TEST_PENDING;
import static com.clarolab.util.Constants.API_AUTOMATED_TEST_URI;
import static com.clarolab.util.Constants.FILTERS;
import static com.clarolab.util.Constants.LIST_PATH;
import static com.clarolab.util.Constants.UPDATE_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public abstract class AutomatedTestIssueAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private AutomatedTestIssueRepository automatedTestIssueRepository;

    @Autowired
    private AutomatedTestIssueMapper automatedTestIssueMapper;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Autowired
    private TestExecutionService testExecutionService;


    // Suite variables definitions

    protected AutomatedTestIssueDTO automatedTestIssueDTO;


    @Before
    public void setUpAbstract() {
        provider.clear();

        TestTriage triage = provider.getTestCaseTriage();
        triage.setExecutor(provider.getExecutor());
        TestTriageDTO triageDTO = testTriageMapper.convertToDTO(triage);

        // create DTO Automated Issue
        automatedTestIssueDTO = provider.getAutomatedTestIssueDTO(triageDTO);
    }


    // Abstract test definitions

    @Test
    public abstract void testDeleteAutomatedTestIssue();


    // Non-abstract test definitions

    @Test
    public void testCreateAutomatedTestIssue() {
        stepsCreateAutomatedTestIssue(null)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(AutomatedTestIssueDTO.class);
    }

    @Test
    public void testGetAutomatedTestIssue() {
        AutomatedTestIssueDTO createdDTO = stepsCreateAutomatedTestIssue(null).as(AutomatedTestIssueDTO.class);
        int id = createdDTO.getId().intValue();

        given()
                .get(API_AUTOMATED_TEST_URI + Constants.GET + "/" + id)
                .then().statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(id))
                .extract().as(AutomatedTestIssueDTO.class);
    }

    @Test
    public void testListAutomatedTestIssue() {
        AutomatedTestIssueDTO createdDTO = stepsCreateAutomatedTestIssue(null).as(AutomatedTestIssueDTO.class);

        Response response = given()
                .get(API_AUTOMATED_TEST_URI + LIST_PATH);

        response.then().statusCode(HttpStatus.SC_OK);

        List<AutomatedTestIssueDTO> content = response.jsonPath().getList("content", AutomatedTestIssueDTO.class);
        int resultSetSize = Integer.parseInt(response.jsonPath().get("totalElements").toString());

        // Check resultset in response is not empty
        Assert.assertTrue(resultSetSize > 0);

        // Check totalElements value matches with elements in resultset
        Assert.assertEquals(resultSetSize, content.size());

        // Check the created AutomatedIssue is present in resultset
        Assert.assertTrue(content.stream().anyMatch(ati -> createdDTO.getId().compareTo(ati.getId()) == 0));
    }

    @Test
    public void testGetPersistence() {
        int amount = 5;
        AutomatedTestIssue entity = provider.getAutomatedTestIssue();

        for (int i = 0; i < amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i+1);
            provider.getTestExecution();
        }
        automatedTestIssueService.save(provider.getAutomatedTestIssue());

        given()
                .get(API_AUTOMATED_TEST_URI + LIST_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body("content", not(empty()));
    }

    @Test
    public void automationTwoFailures() {
        provider.setName("TwoFailures1");
        provider.getBuild(1);
        TestExecution test = provider.getTestExecutionFail();
        TestTriage triage = provider.getTestCaseTriage();

        TestTriageDTO triageDTO = testTriageMapper.convertToDTO(triage);

        // create DTO Automated Issue
        AutomatedTestIssueDTO dto = provider.getAutomatedTestIssueDTO(triageDTO);

        // call endpoint to create the automated issue
        AutomatedTestIssueDTO dtoReturned = given()
                .body(dto)
                .when()
                .contentType(ContentType.JSON)
                .post(API_AUTOMATED_TEST_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(AutomatedTestIssueDTO.class);


        // BUILD 2
        provider.setName("TwoFailures2");
        provider.setBuild(null);
        provider.setBuildTriage(null);
        provider.setTestExecution(null);
        provider.getBuild(2);
        TestExecution test2 = provider.getTestExecution();
        test2.setStatus(StatusType.FAIL);
        test2.setName(test.getName());
        testExecutionService.update(test2);
        TestTriage triage2 = provider.getTestCaseTriage();

        TestTriageDTO triage2DTO = testTriageMapper.convertToDTO(triage2);

        // create DTO Automated Issue
        AutomatedTestIssueDTO dto2 = provider.getAutomatedTestIssueDTO(triage2DTO);

        // call endpoint to create the automated issue
        AutomatedTestIssueDTO dtoReturned2 = given()
                .body(dto)
                .when()
                .contentType(ContentType.JSON)
                .put(API_AUTOMATED_TEST_URI + UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(AutomatedTestIssueDTO.class);

        TestTriage dbTriage = testTriageService.find(triage.getId());

        Assert.assertNotNull(dbTriage);
        Assert.assertNotNull(dbTriage.getTestCase());

        Assert.assertEquals(dbTriage.getTestCase().getId(),dtoReturned.getTestCaseId());

    }

    @Test
    public void testFilterAutomatedTestIssue() {
        filterAutomatedTestIssues(FilterParam.builder().build(), null)
                .then().statusCode(HttpStatus.SC_OK).extract().as(RestPageImpl.class);
    }

    @Test
    public void testAssigneeFilterAutomatedTestIssue() {
        AutomatedTestIssue otherUserAutomatedTestIssue = createAutomatedTestIssueForAnotherUser();

        // Request for current user's AutomatedIssues
        List<AutomatedTestIssueDTO> response = filterAutomatedTestIssues(FilterParam.builder().assignee(true).build(), null)
                .then().statusCode(HttpStatus.SC_OK).extract().jsonPath().getList("content", AutomatedTestIssueDTO.class);

        // Check that all returned AutomatedIssues in result set does not belong to current user
        if (response.size() > 0)
            Assert.assertTrue(response.stream().allMatch(i -> !username.equals(i.getTriager().getUsername())));

        response = filterAutomatedTestIssues(FilterParam.builder().assignee(false).build(),null)
                .then().statusCode(HttpStatus.SC_OK).extract().jsonPath().getList("content", AutomatedTestIssueDTO.class);

        Assert.assertTrue(
                !response.isEmpty() &&
                        response.stream().filter(i -> i.getId().compareTo(otherUserAutomatedTestIssue.getId()) == 0).findFirst().isPresent()
        );
    }

    private Response filterAutomatedTestIssues(FilterParam filterParam, Map<String, ?> queryParams) {
        Map<String, ?> parameters = (queryParams == null) ? new HashMap<>(): queryParams;
        return given()
                .queryParams(parameters)
                .queryParam("filter", filterParam.toAutomatedTestIssueJsonString() )
                .get(API_AUTOMATED_TEST_URI + LIST_PATH + FILTERS);
    }

    @Test
    public void testOpenPendingAutomatedTestIssue() {
        stepsPendingAutomatedTestIssue(IssueType.OPEN);
    }

    @Test
    public void testReopenPendingAutomatedTestIssue() {
        stepsPendingAutomatedTestIssue(IssueType.REOPEN);
    }

    @Test
    public void testUpdateAutomatedTestIssue() {
        // New Type value to set in update
        String newType = IssueType.CRITICAL.toString();

        // Create AutomatedIssue and save
        AutomatedTestIssueDTO originalDTO = stepsCreateAutomatedTestIssue(null).as(AutomatedTestIssueDTO.class);
        AutomatedTestIssue originalEntity = automatedTestIssueService.save(automatedTestIssueMapper.convertToEntity(originalDTO));

        // Check the created AutomatedIssue hasn't type OPEN
        Assert.assertNotEquals(newType, originalDTO.getIssueType());
        Assert.assertNotEquals(newType, originalDTO.getIssueType());

        // Set new Type value and update
        originalDTO.setIssueType(newType);
        AutomatedTestIssueDTO updatedDTO = given()
                .when()
                .contentType(ContentType.JSON)
                .body(originalDTO)
                .put(API_AUTOMATED_TEST_URI + Constants.UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(AutomatedTestIssueDTO.class);

        // Validate in response that the Type and updated timestamp were updated
        Assert.assertEquals(newType, updatedDTO.getIssueType());
        Assert.assertTrue(originalDTO.getUpdated() < updatedDTO.getUpdated());

        // Query the AutomatedIssue from database and validate both, updated time and Type values
        AutomatedTestIssue updatedEntity = automatedTestIssueRepository.findById(originalEntity.getId()).get();
        Assert.assertEquals(originalDTO.getIssueType(), updatedEntity.getIssueType().toString());
        Assert.assertTrue(originalEntity.getUpdated() < updatedEntity.getUpdated());
    }


    // Test steps methods

    protected Response stepsCreateAutomatedTestIssue(AutomatedTestIssueDTO body) {
        return given()
                .body((body == null) ? automatedTestIssueDTO : body)
                .when()
                .contentType(ContentType.JSON)
                .post(API_AUTOMATED_TEST_URI + Constants.CREATE_PATH);
    }

    public void stepsPendingAutomatedTestIssue(IssueType issueType) {
        User currentUserEntity = userService.findByUsername(username);

        // Get current user's pending AutomatedIssues at this moment
        Long currentPendings = automatedTestIssueRepository.countAllButFixed(currentUserEntity);

        // Set current user to AutomatedIssue
        automatedTestIssueDTO.setTriager(userMapper.convertToDTO(currentUserEntity));

        // Set OPEN Type and save
        automatedTestIssueDTO.setIssueType(issueType.toString());
        automatedTestIssueService.save(automatedTestIssueMapper.convertToEntity(automatedTestIssueDTO));

        // Request to service
        String response = requestPendingAutomatedTestIssues()
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(String.class);

        // Validate expected
        Assert.assertTrue(Integer.parseInt(response) == currentPendings.intValue() + 1);
    }

    protected Response stepsDeleteAutomatedTestIssue() {
        User currentUserEntity = userService.findByUsername(username);
        automatedTestIssueDTO.setTriager(userMapper.convertToDTO(currentUserEntity));
        AutomatedTestIssue automatedTestIssue = automatedTestIssueService.save(automatedTestIssueMapper.convertToEntity(automatedTestIssueDTO));

        return given()
                .delete(API_AUTOMATED_TEST_URI + Constants.DELETE + "/" + automatedTestIssue.getId());
    }

    private Response requestPendingAutomatedTestIssues() {
        return given().get(API_AUTOMATED_TEST_URI + API_AUTOMATED_TEST_PENDING);
    }

    private AutomatedTestIssue createAutomatedTestIssueForCurrentUser() {
        return createAutomatedTestIssueForGivenUser(userService.findByUsername(username));
    }

    private AutomatedTestIssue createAutomatedTestIssueForAnotherUser() {
        User user = DataProvider.getUserAsViewer();
        user.setUsername(RandomStringUtils.randomAlphabetic(50));
        user = userService.save(user);
        return createAutomatedTestIssueForGivenUser(user);
    }

    // Replaces the global AutomatedTestIssueDTO for global usage in tests and returns instance created
    private AutomatedTestIssue createAutomatedTestIssueForGivenUser(User userEntity) {
        TestTriage triage = provider.getTestCaseTriage();
        TestTriageDTO triageDTO = testTriageMapper.convertToDTO(triage);
        AutomatedTestIssueDTO automatedTestIssueDTO = provider.getAutomatedTestIssueDTO(triageDTO);
        // If userEntity is null, it means its Triager value is the default one; another one excepts the current test user.
        if (userEntity != null)
            automatedTestIssueDTO.setTriager(userMapper.convertToDTO(userEntity));
        this.automatedTestIssueDTO = automatedTestIssueDTO;
        return automatedTestIssueService.save(automatedTestIssueMapper.convertToEntity(automatedTestIssueDTO));
    }

}
