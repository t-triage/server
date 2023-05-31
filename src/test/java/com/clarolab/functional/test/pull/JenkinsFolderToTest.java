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

public class JenkinsFolderToTest extends BaseFunctionalTest {

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
        containerService.populateAtomic(provider.getContainer().getId());

        List<Executor> executors = executorService.findAllByContainerAndEnabled(provider.getContainer(), true);

        // Executor Validation
        Assert.assertEquals("Amount of executors in container", 11, executors.size());

        Executor junit = executors.stream().filter(executor -> executor.getName().equals("Junit Jenkins Test")).findFirst().orElse(null);
        Assert.assertNotNull(junit);

        // Build Validation
        List<Build> builds = buildService.findAll(junit);
        Assert.assertEquals("Amount of builds should be DEFAULT_MAX_BUILDS_TO_PROCESS", DEFAULT_MAX_BUILDS_TO_PROCESS, builds.size());
        Build build = buildService.getLastBuild(junit);

        // Test Validation
        Assert.assertTrue("Build should have tests", build.getTestCases().size() > 0);

        TestExecution dataProvider = find("failDuplicated", "DuplicateClassSampleTest", build.getTestCases());
        Assert.assertEquals("Test should be duplicated i.e. dataprovider", true, dataProvider.getTestCase().isDataProvider());

        TestExecution fail = find("fail", "JunitSampleTest", build.getTestCases());
        Assert.assertEquals("It should not be data provider", false, fail.getTestCase().isDataProvider());
        Assert.assertEquals("It should be failed test", StatusType.FAIL, fail.getStatus());

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
            ciConnector = ciConnectors.get("JENKINS_CLB").connect();
        }
        return ciConnector;
    }

    private void initializeProvider() {
        if (!initialized) {
            //initialized = true;
            provider.clear();

            Connector connector = provider.getConnector();
            connector.setName("JenkinsClarolab");
            connector.setUrl("http://dev.clarolab.com:12080/");
            connector.setType(ConnectorType.JENKINS);
            connector.setUserName("admin");
            connector.setUserToken("115977a708eaccbfcc825e86ba0a368fb9");
            connectorService.update(connector);

            Container container = provider.getContainer();
            container.setName("Folder To Use By Junits");
            container.setHiddenData("bitbucket/TTriage/FolderToUseByJunits");
            container.setUrl("http://dev.clarolab.com:12080/view/FolderToUseByJunits/");
            container.setPopulateMode(PopulateMode.PULL);
            containerService.update(container);

            ciConnector = ciConnectors.get("JENKINS_CLB").connect();
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
