/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.model.*;
import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class SecurityTestData extends AbstractTestData {

    @Override
    public void populate() {
        createUser();
        createProduct();
        createMilestone();
        createConnector();
        createContainer();
        createExecutor();
        createTestExecution();

    }

    private void createUser() {
        provider.setUser(null);
        User entry = provider.getUser();

        entry.setRealname("entry name1");
        entry.setUsername("entrydescription@test.com");

        userService.update(entry);
    }

    private void createProduct() {
        provider.setProduct(null);
        Product entry = provider.getProduct();

        entry.setName("entry name1");
        entry.setDescription("entry description");
        entry.setPackageNames("packageName");
        entry.setLogo(null);

        productService.update(entry);
    }

    private void createMilestone() {
        provider.setDeadline(null);
        Deadline entry = provider.getDeadline();

        entry.setName("entry name1");
        entry.setDescription("entry description");

        deadlineService.update(entry);
    }

    private void createConnector() {
        provider.setConnector(null);
        Connector entry = provider.getConnector();

        entry.setName("entry name1");

        connectorService.update(entry);
    }

    private void createContainer() {
        provider.clearContainer();
        Container entry = provider.getContainer();

        entry.setName("entry name1");

        containerService.update(entry);
    }

    private void createExecutor() {
        provider.setExecutor(null);
        Executor entry = provider.getExecutor();

        entry.setName("entry name1");

        executorService.update(entry);
    }

    private void createTestExecution() {
        provider.clearForNewBuild();
        provider.setTestExecution(null);
        provider.getBuild(1);
        TestTriagePopulate test = new TestTriagePopulate();
        test.setAs(StatusType.FAIL, 0, 10);

        test.setTestCaseName("Test Name");
        test.setErrorDetails("Error Detail");
        test.setErrorStackTrace("Error stack trace");

        provider.getTestExecution(test);
        TestTriage testTriage = provider.getTestCaseTriage();

        testTriage.setNote(getNote());
        createProductIssue(testTriage, "Jira Description");
        createTestIssue(testTriage);

        testTriageService.update(testTriage);
    }

    private Note getNote() {
        provider.setNote(null);
        Note entry = provider.getNote();

        entry.setDescription("entry name1");

        noteService.update(entry);

        return entry;
    }


}
