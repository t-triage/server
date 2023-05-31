/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.manual;

import com.clarolab.dto.DateStatsDTO;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.event.analytics.ManualTestStat;
import com.clarolab.event.analytics.ManualTestStatService;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.Product;
import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.service.ManualTestRequirementService;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.TechniqueType;
import com.clarolab.model.manual.types.TestPriorityType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.serviceDTO.ManualTestCaseServiceDTO;
import com.clarolab.serviceDTO.ManualTestExecutionServiceDTO;
import com.clarolab.serviceDTO.TestExecutionServiceDTO;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ManualTestCaseFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestRequirementService manualTestRequirementService;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestExecutionServiceDTO manualTestExecutionServiceDTO;

    @Autowired
    private TestExecutionServiceDTO testExecutionServiceDTO;

    @Autowired
    private ManualTestCaseServiceDTO manualTestCaseServiceDTO;

    @Autowired
    private ManualTestStatService  manualTestStatService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        ManualTestCase test = provider.getManualTestCase(2);
        ManualTestCase dbTest = manualTestCaseService.find(test.getId());

        Assert.assertNotNull("Test should have been created in the DB", dbTest);
    }

    @Test
    public void searchProduct() {
        Product product = provider.getProduct();
        ManualTestCase test1 = provider.getManualTestCase(4);
        provider.setManualTestCase(null);
        ManualTestCase test2 = provider.getManualTestCase(5);
        provider.setManualTestCase(null);
        provider.setProduct(null);
        ManualTestCase manchiTest = provider.getManualTestCase(3);
        List<ManualTestCase> manualTestsQuery = manualTestCaseService.findAllByProduct(product);
        for (ManualTestCase manualTestCase: manualTestsQuery) {
            Assert.assertEquals(manualTestCase.getProduct(), product);
        }
    }

    @Test
    public void searchTestName() {
        String name = "TestName";
        String searchString = " Tes nAm ";
        provider.setName(name);
        ManualTestCase test = provider.getManualTestCase(2);
        provider.setManualTestCase(null);
        ManualTestCase test1 = provider.getManualTestCase(3);
        List<ManualTestCase> manualTests = new ArrayList<>();
        manualTests.add(test1);
        manualTests.add(test);
        List<ManualTestCase> manualTestsQuery = manualTestCaseService.searchByName(searchString);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTests, manualTestsQuery));
    }

    @Test
    public void searchFunctionality() {
        String name = "TestName";
        String searchString = "Tes nAme";
        provider.setName("random test name");
        ManualTestCase test = provider.getManualTestCase(2);
        test.setFunctionality(name);
        test.getFunctionalityEntity().setName(name);
        provider.setManualTestCase(null);
        List<String> manualTests = new ArrayList<>();
        manualTests.add(test.getFunctionality());
        List<String> manualTestsQuery = manualTestCaseService.searchFunctionality(searchString);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTests, manualTestsQuery));
    }

    @Test
    public void searchFunctionalityEntity() {
        String searchString = "func";
        provider.setName("random test name");
        ManualTestCase test = provider.getManualTestCase(2);
        test.setFunctionalityEntity(provider.getFunctionalityEntity());
        provider.setManualTestCase(null);
        List<String> manualTests = new ArrayList<>();
        manualTests.add(test.getFunctionalityEntity().getName());
        List<String> manualTestsQuery = manualTestCaseService.searchFunctionality(searchString);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTests, manualTestsQuery));
    }

    @Test
    public void searchByUser() {
        User user = provider.getUser();
        ManualTestCase userTest1 = provider.getManualTestCase(2);
        provider.setManualTestCase(null);
        ManualTestCase userTest2 = provider.getManualTestCase(8);
        provider.setManualTestCase(null);
        provider.setUser(null);
        ManualTestCase manchiTest = provider.getManualTestCase(2);
        List<ManualTestCase> manualTests = new ArrayList<>();
        manualTests.add(userTest1);
        manualTests.add(userTest2);
        List<ManualTestCase> manualTestsQuery = manualTestCaseService.findAllByOwner(user);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTests, manualTestsQuery));
    }

    @Test
    public void filteredSearch() {
        String oldName = provider.getName();
        provider.getContainer();
        User user = provider.getUser();


        //Test 1
        provider.setName("Check the response of entering a valid user and password ");
        ManualTestCase test1 = provider.getManualTestCase(4);
        ManualTestExecution manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setLastExecutionTime(DateUtils.offSetDays(-2)); // set last execution = last 3 days
        manualTestExecutionService.update(manualTestExecution);
        //provider.setManualTestExecution(null);

        ManualTestRequirement req = test1.getRequirement();
        req.setName("Have a valid account");
        manualTestRequirementService.update(req);
        provider.setManualTestRequirement(null);

        ManualTestStep step = test1.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test1.getSteps().get(1);
        step.setStep("Enter username");
        step.setData("username: 'valid_username'");
        manualTestStepService.update(step);

        step = test1.getSteps().get(2);
        step.setStep("Enter password");
        step.setData("password: 'valid_password'");
        manualTestStepService.update(step);

        step = test1.getSteps().get(3);
        step.setStep("Click login button");
        step.setExpectedResult("Login successful");
        manualTestStepService.update(step);

        ArrayList<TechniqueType> list = Lists.newArrayList();
        list.add(TechniqueType.BUSINESS_RISK);
        list.add(TechniqueType.PERFORMANCE);

        test1.setTechniques(list);
        test1.setAutomationStatus(AutomationStatusType.NO);
        test1.setFunctionality("functionality");
        test1.setFunctionalityEntity(provider.getFunctionalityEntity());
        manualTestCaseService.update(test1);

        provider.setManualTestCase(null);


        //Test 2

        provider.setName("Check if new product is created ");
        ManualTestCase test2 = provider.getManualTestCase(3);
        test2.setNeedsUpdate(true);
        test2.setFunctionality("functionality");
        test2.setFunctionalityEntity(provider.getFunctionalityEntity());
        manualTestCaseService.update(test2);

        manualTestExecution.setLastExecutionTime(DateUtils.offSetDays(-1)); // set last execution = yesterday
        manualTestExecutionService.update(manualTestExecution);
        provider.setManualTestExecution(null); // reset last execution in manual test execution

        req = test2.getRequirement();
        req.setName("Have a valid product");
        manualTestRequirementService.update(req);

        step = test2.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test2.getSteps().get(1);
        step.setStep("Navigate to products page");
        step.setData("example.com/product");
        manualTestStepService.update(step);

        step = test2.getSteps().get(2);
        step.setStep("Use search bar to find desired product");
        step.setExpectedResult("Product should appear");
        manualTestStepService.update(step);

        provider.setTestExecution(null);
        provider.setManualTestCase(null);


        //Test 3

        provider.setUser(null);
        User updater = provider.getUser();
        provider.setUser(null);
        provider.getUser();
        provider.setName("Create main content");
        ManualTestCase test3 = provider.getManualTestCase(5);
        test3.setAutomationStatus(AutomationStatusType.DONE);
        test3.setAutomatedTestCase(provider.getTestCase());
        test3.setRequirement(null);
        test3.setMainStep(test3.getSteps().get(3));
        test3.setFunctionality("Different funcionality");
        test3.setFunctionalityEntity(provider.getFunctionalityEntity());
        test3.setPriority(TestPriorityType.LOW);
        test3.setLastUpdater(updater);
        manualTestCaseService.update(test3);

        step = test3.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test3.getSteps().get(1);
        step.setStep("Navigate to content page");
        step.setData("main");
        manualTestStepService.update(step);

        step = test3.getSteps().get(2);
        step.setStep("Press Create menu > Create Content");
        step.setData("Fill with all kinds of characters i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com");
        step.setExpectedResult("Content should be created");
        manualTestStepService.update(step);

        step = test3.getSteps().get(3);
        step.setStep("Press Create menu > Create Content");
        step.setData("Fill with all kinds of characters i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com");
        step.setExpectedResult("Content should be created");
        manualTestStepService.update(step);

        step = test3.getSteps().get(4);
        step.setStep("Go to home page");
        step.setData("");
        step.setExpectedResult("Content should appear");
        manualTestStepService.update(step);


        //Target Filters
        List<ManualTestCase> ownerFilter = new ArrayList<>();
        ownerFilter.add(test1);
        ownerFilter.add(test2);

        List<ManualTestCase> techniquesFilter = new ArrayList<>();
        techniquesFilter.add(test1);

        List<ManualTestCase> needsUpdateFilter = new ArrayList<>();
        needsUpdateFilter.add(test1);
        needsUpdateFilter.add(test3);

        List<ManualTestCase> requirementFilter = new ArrayList<>();
        requirementFilter.add(test2);

        List<ManualTestCase> nameFilter = new ArrayList<>();
        nameFilter.add(test1);

        List<ManualTestCase> suiteFilter = new ArrayList<>();
        suiteFilter.add(test1);
        suiteFilter.add(test2);
        suiteFilter.add(test3);

        List<ManualTestCase> componentFilter = new ArrayList<>();
        componentFilter.add(test3);

        List<ManualTestCase> functionalityFilter = new ArrayList<>();
        functionalityFilter.add(test1);
        functionalityFilter.add(test2);
        functionalityFilter.add(test3);

        List<ManualTestCase> priorityFilter = new ArrayList<>();
        priorityFilter.add(test1);
        priorityFilter.add(test2);

        List<ManualTestCase> automationStatusFilter = new ArrayList<>();
        automationStatusFilter.add(test2);

        List<ManualTestCase> lastUpdaterFilter = new ArrayList<>();
        lastUpdaterFilter.add(test3);

        List<ManualTestCase> idFilter = new ArrayList<>();
        idFilter.add(test2);

        List<ManualTestCase> lastThreeDaysExecutionTests = new ArrayList<>(Arrays.asList(test1, test2));

        //
        Assert.assertTrue(CollectionUtils.isEqualCollection(ownerFilter, manualTestCaseService.findAllFiltered(null, null, null, user.getId(), null, null, null, null, null, null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(techniquesFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, 12, null, null, null, null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(needsUpdateFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, false, null, null, null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(requirementFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, test2.getRequirement().getName(), null, null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(nameFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, test1.getName(), null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(suiteFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, test1.getSuite().name(), null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(componentFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, null, test3.getComponent2().getId(), null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(functionalityFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null, null, test1.getFunctionalityEntity().getId(), null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(priorityFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null, null, null, test1.getPriority().getPriority(),null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(automationStatusFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,test2.getAutomationStatus().getType(), null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(idFilter, manualTestCaseService.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null, test2.getId(), null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(lastThreeDaysExecutionTests, manualTestCaseService.findAllFiltered(null, null, DateUtils.offSetDays(-3), null, null, null, null, null, null, null, null, null, null, null, null,null, null, null, null)));
        Assert.assertTrue(CollectionUtils.isEqualCollection(lastUpdaterFilter, manualTestCaseService.findAllFiltered(null, null, null, test3.getLastUpdater().getId(), null, null, null, null, null, null, null, null, null, null, null,null, null, null, null)));

        provider.setName(oldName);
    }

    @Test
    public void searchErrorsManualTest() {
        provider.clear();
        ManualTestStat data1 = provider.getManualTestStat();
        provider.clear();
        ManualTestStat data2 = provider.getManualTestStat();

        ManualTestStat datasame1 = manualTestStatService.find(data1.getId());
        Assert.assertNotNull(data1);
        Assert.assertNotNull(data2);
        Assert.assertEquals(data1, datasame1);

        DateStatsDTO dto1 = new DateStatsDTO(data1.getUpdated(),1L);
        DateStatsDTO dto2= new DateStatsDTO(data2.getUpdated(),1L);

        List<DateStatsDTO> list = new ArrayList<>();
        list = testExecutionServiceDTO.searchErrorTestStats();

        Assert.assertNotNull(list);
        Assert.assertEquals(dto1,list.get(0));
        Assert.assertEquals(dto2,list.get(1));
        Assert.assertEquals(2,list.size());
        Assert.assertEquals(((Long) data1.getUpdated()), list.get(0).getFailExecutionDate());

    }

    @Test
    public void searchManualTestCasesSince() throws ParseException {
        ManualTestCase testCase = provider.getManualTestCase(4);
        Assert.assertEquals(testCase , manualTestCaseService.find(testCase.getId()));
        testCase.setTimestamp(1591013718000L); // update the timestamp to oldest like monday, 1 de june de 2020
        manualTestCaseService.update(testCase);

        long current = DateUtils.convertDate("05/31/2022 15:19:08:000","MM/dd/yyyy HH:mm:ss:SSS" ); //choice the date to filter
        List <ManualTestCaseDTO> dto = manualTestCaseServiceDTO.getManualTestCasesSince(current);
        System.out.println(dto.size());

        Assert.assertFalse(dto.contains(testCase));
    }

    @Test
    public void searchManualTestExecutionSince() throws ParseException {
        ManualTestExecution testExecution = provider.getManualTestExecution();
        Assert.assertEquals(testExecution , manualTestExecutionService.find(testExecution.getId()));
        testExecution.setTimestamp(1591013718000L); // update the timestamp to oldest like monday, 1 de june de 2020
        manualTestExecutionService.update(testExecution);

        long current1 = DateUtils.convertDate("05/31/2022 15:19:08:000","MM/dd/yyyy HH:mm:ss:SSS" );    //choice the date to filter
        List <ManualTestExecutionDTO> dto = manualTestExecutionServiceDTO.findManualTestExecutionSinceDTO(current1);
        System.out.println(dto.size());

        Assert.assertFalse(dto.contains(testExecution));
    }

}
