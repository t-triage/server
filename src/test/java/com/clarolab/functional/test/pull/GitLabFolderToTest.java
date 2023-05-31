/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.pull;

import com.clarolab.connectors.CIConnector;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.*;
import org.hamcrest.Matchers;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.DEFAULT_MAX_BUILDS_TO_PROCESS;

public class GitLabFolderToTest extends BaseFunctionalTest {

    boolean initialized = false;
    private CIConnector ciConnector;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private BuildService buildService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Before
    public void clearProvider() {
        initializeProvider();

        Assume.assumeThat(connector().isConnected(), Matchers.is(true));
    }

    @Ignore
    @Test
    public void getExecutorsCleanContainer() {
        provider.setUseRandom(true);

        containerService.populateAtomic(provider.getContainer().getId());

        List<Executor> executors = executorService.findAllByContainerAndEnabled(provider.getContainer(), true);

        // Executor Validation
        Assert.assertEquals("Amount of executors in container", 6, executors.size());

        Executor junit = executors.stream().filter(executor -> executor.getName().equals("junitTests")).findFirst().orElse(null);
        Assert.assertNotNull(junit);

        // Build Validation
        List<Build> builds = buildService.findAll(junit);
        Assert.assertEquals("Amount of builds should be DEFAULT_MAX_BUILDS_TO_PROCESS", DEFAULT_MAX_BUILDS_TO_PROCESS, builds.size());
        Build build = buildService.getLastBuild(junit);

        // Test Validation
        Assert.assertTrue("Build should have tests", build.getTestCases().size() > 0);

        TestExecution dataProvider = find("failJUTest", "JunitTest", build.getTestCases());
        Assert.assertEquals("This test is ok to fail", StatusType.FAIL, dataProvider.getStatus());

        TestExecution fail = find("passJUTest", "JunitTest", build.getTestCases());
        Assert.assertEquals("This test should pass", StatusType.PASS, fail.getStatus());


        // TriageAgent
        provider.setExecutor(junit);
        provider.setBuild(build);
        BuildTriage buildTriage = provider.getBuildTriage();

        // Assert Build Triage
        Assert.assertNotNull("There should be a BuildTriage generated", buildTriage);
        Assert.assertEquals("Build should have been processed since it has generated a buildTriage", true, buildTriage.getBuild().isProcessed());

        // Assert TestTriage
        List<TestTriage> triages = testTriageService.findAllByBuild(build);
        Assert.assertEquals("There should be created a TestTriage for each TestExecution", build.getTestCases().size(), triages.size());

    }


    private CIConnector connector() {
        if (ciConnector == null) {
            ciConnector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        }
        return ciConnector;
    }

    private void initializeProvider() {
        if (!initialized) {
            initialized = true;

            Connector connector = provider.getConnector();
            connector.setName("GITLAB_FLUXIT");
            connector.setUrl("https://circleci.com/");
            connector.setType(ConnectorType.CIRCLECI);
            connector.setUserName("TTriage");
            connector.setUserToken("a296eed321ab07d489eae6a2eed301eac5472b19");
            connectorService.update(connector);

            Container container = provider.getContainer();
            container.setName("Folder To Use By CircleCI");
            container.setHiddenData("bitbucket/TTriage/qa-reports-ci-tests");
            container.setUrl("https://circleci.com/bb/TTriage/qa-reports-ci-tests/");
            container.setPopulateMode(PopulateMode.PULL);
            containerService.update(container);

            ciConnector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        }

    }

    private TestExecution find(String name, String path, List<TestExecution> tests) {
        for (TestExecution test : tests) {
            if (name.equals(test.getName()) && test.getLocationPath().contains(path)) {
                return test;
            }
        }
        return null;
    }

}
