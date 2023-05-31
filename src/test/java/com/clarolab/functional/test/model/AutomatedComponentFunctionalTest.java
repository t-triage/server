/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.TestCase;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.AutomatedComponentService;
import com.clarolab.service.TestComponentRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.MAX_AUTOMATED_COMPONENTS_ALLOWED;

public class AutomatedComponentFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private AutomatedComponentService automatedComponentService;

    @Autowired
    private TestComponentRelationService testComponentRelationService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testCreate() {
        AutomatedComponent test = provider.getAutomatedComponent();
        AutomatedComponent dbTest = automatedComponentService.find(test.getId());

        Assert.assertNotNull("AutomatedComponent should have been created in the DB.", dbTest);
    }

    @Test
    public void testSearchAutomatedComponentByName() {
        String name = "SearchComponent";
        provider.setName(name);

        provider.getAutomatedComponent();

        List<AutomatedComponent> result = automatedComponentService.search(name);

        Assert.assertEquals("AutomatedComponent should be found", 1, result.size());
    }

    @Test
    public void testSetComponentToTests() {
        int amount = 6;
        List<Long> testCasesIds = new ArrayList<>(amount);
        AutomatedComponent automatedComponent = provider.getAutomatedComponent();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCasesIds.add(provider.getTestCase().getId());
        }

        automatedComponentService.setComponentToTests(automatedComponent, testCasesIds);
        List<TestComponentRelation> relations = automatedComponentService.findAllByComponent(automatedComponent);

        Assert.assertEquals("Not all relation were created", amount, relations.size());

    }

    @Test
    public void testMaxComponentsAllowedPerTestCase() {
        int amount = 10;
        List<Long> testCasesIds = new ArrayList<>(amount);

        provider.setTestExecution(null);
        TestCase testCase = provider.getTestCase();
        testCasesIds.add(testCase.getId());

        for (int i = 0; i < amount; i++) {
            provider.setAutomatedComponent(null);
            automatedComponentService.setComponentToTests(provider.getAutomatedComponent(), testCasesIds);
        }

        long relations = testComponentRelationService.countByTestCase(testCase);

        Assert.assertTrue(String.format("Each test should no have more than %d components", MAX_AUTOMATED_COMPONENTS_ALLOWED), relations <= MAX_AUTOMATED_COMPONENTS_ALLOWED);

    }

    @Test
    public void testSetComponentToTestsWithDuplicates() {
        int amount = 6;
        List<Long> testCasesIds = new ArrayList<>(amount);
        AutomatedComponent automatedComponent = provider.getAutomatedComponent();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCasesIds.add(provider.getTestCase().getId());
        }

        automatedComponentService.setComponentToTests(automatedComponent, testCasesIds);

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCasesIds.add(provider.getTestCase().getId());
        }

        automatedComponentService.setComponentToTests(automatedComponent, testCasesIds);
        List<TestComponentRelation> relations = automatedComponentService.findAllByComponent(automatedComponent);

        Assert.assertEquals("Not all relation were created", amount * 2, relations.size());

    }

    @Test
    public void testDeleteComponentFromTest() {
        int amount = 6;
        List<Long> testCasesIds = new ArrayList<>(amount);
        AutomatedComponent automatedComponent = provider.getAutomatedComponent();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCasesIds.add(provider.getTestCase().getId());
        }

        automatedComponentService.setComponentToTests(automatedComponent, testCasesIds);
        List<TestComponentRelation> allRelations = automatedComponentService.findAllByComponent(automatedComponent);

        TestComponentRelation toDelete = allRelations.get(0);
        Long deletedId = testComponentRelationService.delete(toDelete.getTestCase(), toDelete.getComponent());

        Assert.assertEquals("Deleted relation doesn't match", deletedId, toDelete.getId());

        allRelations = automatedComponentService.findAllByComponent(automatedComponent);

        Assert.assertEquals("Deleted relations amount didn't change after the deletion", amount - 1, allRelations.size());

    }

    @Test
    public void testSuggestedDefault() {
        int amount = 3;
        List<Long> testCasesIds = new ArrayList<>(amount);
        List<AutomatedComponent> expectedComponents = new ArrayList<>();

        provider.setAutomatedComponent(null);
        AutomatedComponent component1 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component2 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component3 = provider.getAutomatedComponent();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCasesIds.add(provider.getTestCase().getId());
        }

        automatedComponentService.setComponentToTests(component1, testCasesIds);
        automatedComponentService.setComponentToTests(component2, testCasesIds);

        provider.setTestExecution(null);
        testCasesIds.clear();
        testCasesIds.add(provider.getTestCase().getId());
        automatedComponentService.setComponentToTests(component3, testCasesIds);

        List<AutomatedComponent> suggestedComponents = automatedComponentService.suggestedDefaultComponents();
        expectedComponents.add(component1);
        expectedComponents.add(component3);

        Assert.assertTrue("Suggested components aren't the first component of each test", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

        expectedComponents.add(component2);
        Assert.assertFalse("Component2 shouldn't be suggested", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

    }

    @Test
    public void testSuggestedAutomatedComponents() {
        List<Long> testCasesIds = new ArrayList<>();
        List<AutomatedComponent> suggestedComponents;
        List<AutomatedComponent> expectedComponents = new ArrayList<>();
        List<Long> assignedComponentsIds = new ArrayList<>();

        // AutomatedComponents creation
        provider.setAutomatedComponent(null);
        AutomatedComponent component1 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component2 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component3 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component4 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component5 = provider.getAutomatedComponent();
        provider.setAutomatedComponent(null);
        AutomatedComponent component6 = provider.getAutomatedComponent();

        // TestCases creation
        provider.setTestExecution(null);
        TestCase testCase1 = provider.getTestCase();
        provider.setTestExecution(null);
        TestCase testCase2 = provider.getTestCase();
        provider.setTestExecution(null);
        TestCase testCase3 = provider.getTestCase();


        // Set AutomatedComponents to some tests
        testCasesIds.add(testCase1.getId());
        automatedComponentService.setComponentToTests(component1, testCasesIds);
        automatedComponentService.setComponentToTests(component2, testCasesIds);
        automatedComponentService.setComponentToTests(component3, testCasesIds);

        testCasesIds.clear();
        testCasesIds.add(testCase2.getId());
        automatedComponentService.setComponentToTests(component4, testCasesIds);
        automatedComponentService.setComponentToTests(component6, testCasesIds);

        testCasesIds.clear();
        testCasesIds.add(testCase3.getId());
        automatedComponentService.setComponentToTests(component5, testCasesIds);
        automatedComponentService.setComponentToTests(component6, testCasesIds);

        // Test 1
        assignedComponentsIds.add(component1.getId());
        expectedComponents.add(component2);
        expectedComponents.add(component3);

        suggestedComponents = automatedComponentService.suggestedComponents(assignedComponentsIds);
        Assert.assertTrue("Suggested components should be: component2, component3", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

        assignedComponentsIds.add(component2.getId());
        expectedComponents.remove(component1);
        expectedComponents.remove(component2);

        suggestedComponents = automatedComponentService.suggestedComponents(assignedComponentsIds);
        Assert.assertTrue("Suggested components should be: component3", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

        assignedComponentsIds.add(component3.getId());
        expectedComponents.remove(component3);

        suggestedComponents = automatedComponentService.suggestedComponents(assignedComponentsIds);
        Assert.assertTrue("There should be no more Components to suggest", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

        // Test 2
        assignedComponentsIds.clear();
        expectedComponents.clear();
        assignedComponentsIds.add(component6.getId());
        expectedComponents.add(component4);
        expectedComponents.add(component5);

        suggestedComponents = automatedComponentService.suggestedComponents(assignedComponentsIds);
        Assert.assertTrue("Suggested components should be: component4, component5", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

        // Test 3
        assignedComponentsIds.clear();
        expectedComponents.clear();
        assignedComponentsIds.add(component1.getId());
        assignedComponentsIds.add(component6.getId());

        suggestedComponents = automatedComponentService.suggestedComponents(assignedComponentsIds);
        Assert.assertTrue("There should be no more Components to suggest", CollectionUtils.isEqualCollection(suggestedComponents, expectedComponents));

    }

}
