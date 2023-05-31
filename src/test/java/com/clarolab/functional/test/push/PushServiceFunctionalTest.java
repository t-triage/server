/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.push;

import com.clarolab.connectors.impl.utils.report.allure.json.entity.AllureTestCase;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.populate.util.ReportsTestHelper;
import com.clarolab.service.BuildService;
import com.clarolab.service.PushService;
import com.google.gson.Gson;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PushServiceFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private PushService pushService;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private BuildService buildService;


    @Before
    public void cleanUp() {
        provider.clear();
    }

    @Ignore
    @Test
    public void testPushingOnePassingUniqueTest() {
        //Creation
        DataDTO dataDTO = provider.getDataDTO(StatusType.PASS);
        pushService.push(dataDTO);

        //Validation
        List<Build> all = buildService.findAll();
        Assert.assertEquals(1, all.stream().filter(build -> build.getNumber() == dataDTO.getBuildNumber()).count());
    }

    @Ignore
    @Test
    public void testPushingTwoPassingUniqueTest() {
        //Creation
        DataDTO dataDTO1 = provider.getDataDTO(StatusType.PASS);
        pushService.push(dataDTO1);

        DataDTO dataDTO2 = provider.getDataDTO(StatusType.PASS);
        pushService.push(dataDTO2);

        //Validation
        List<Build> all = buildService.findAll();
        Assert.assertEquals(1, all.stream().filter(build -> build.getNumber() == dataDTO1.getBuildNumber()).count());
        Assert.assertEquals(1, all.stream().filter(build -> build.getNumber() == dataDTO2.getBuildNumber()).count());
    }

    @Test
    public void dataToJsonString() {
        //Creation
        DataDTO dataDTO1 = provider.getDataDTO(StatusType.PASS);
        pushService.push(dataDTO1);

        //Validation
        System.out.println(dataDTO1.toJsonString());
    }


    @Test
    public void actonProtractor() {
        //Creation
        String report = "{\"testsuites\": {\"testsuite\": {    \"hostname\": \"localhost\",    \"tests\": 8,    \"failures\": 0,    \"name\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",    \"disabled\": 0,    \"time\": 115.702,    \"errors\": 0,    \"testcase\": [        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Set controlled features\",            \"time\": 0.292        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Login\",            \"time\": 24.511        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Navigate to the Connectors page\",            \"time\": 10.531        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Set precondition: Disconnect all connected CRM\",            \"time\": 61.624        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Navigate to the Forms page\",            \"time\": 10.142        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Click the New Form button | The Form Composer's interstitial page is displayed in a new browser tab. It contains two options: Blank Form and Template Form\",            \"time\": 6.851        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected Select the Blank Form option\",            \"time\": 1.653        },        {            \"classname\": \"chrome.Form Composer: CRM Settings does not display when no CRM connected\",            \"name\": \"Form Composer: CRM Settings does not display when no CRM connected 'CRM Settings' Response option does not appear in the Properties tab\",            \"time\": 0.095        }    ],    \"timestamp\": \"2021-03-31T11:04:58\",    \"skipped\": 0}}}";
        DataDTO dataDTO1 = provider.getDataDTO(StatusType.FAIL);
        // dataDTO1.setViewName("QA");
        dataDTO1.setJobId(0L);
        dataDTO1.setJobUrl(null);
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("Failed");
        dataDTO1.setBuildUrl("http://bamboo3.via.act-on.net:8085/browse/QA-PROD-120");
        dataDTO1.getArtifacts().get(0).setContent("https://bamboo.via.act-on.net:8443/download/QA-PROD-JOB1/build_logs/QA-PROD-JOB1-120.log");
        dataDTO1.getArtifacts().get(0).setFileName("QA-PROD-JOB1-120.log");
        dataDTO1.getArtifacts().get(0).setFileType("log");
        dataDTO1.getArtifacts().get(0).setUrl("https://bamboo.via.act-on.net:8443/download/QA-PROD-JOB1/build_logs/QA-PROD-JOB1-120.log");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(report);
        artifact.setFileName("chrome-ProtractorOutputFormComposerCRMSettingsdoesnotdisplaywhennoCRMconnected.xml");
        artifact.setFileType("xml");
        artifact.setUrl("https://bamboo.via.act-on.net:8443/browse/QA-STAG-140/artifact/shared/QA-TES-AR/chrome-ProtractorOutputFormComposerCRMSettingsdoesnotdisplaywhennoCRMconnected.xml");

        dataDTO1.getArtifacts().add(artifact);

        pushService.push(dataDTO1);

        //Validation
        List<Build> all = buildService.findAll();
        Assert.assertEquals(1, all.stream().filter(build -> build.getNumber() == dataDTO1.getBuildNumber()).count());
    }

    @Test
    public void jsonActon1() {
        Container container = provider.getContainer();
        container.setName("QA");
        container.setReportType(ReportType.PROTRACTOR_STEPS);
        containerService.update(container);

        for (int index = 0; index < 5; index++) {
            String jsonText = ReportsTestHelper.getPlainFile(ReportType.PROTRACTOR_STEPS, index);
            DataDTO data = new Gson().fromJson(jsonText, DataDTO.class);
            Build build = pushService.push(data);

            //Validation
            //  Assert.assertEquals(1, build.getNumber() == data.getBuildNumber());
            System.out.println("File: " + index + " has: " + build.getTestCases().size());
            // Assert.assertTrue(build.getTestCases().size() > 2);
        }
    }

    @Test
    public void jsonDisabledExecutor() {
        Container container = provider.getContainer();
        container.setName("QA");
        container.setReportType(ReportType.PROTRACTOR_STEPS);
        containerService.update(container);

        Executor executor = provider.getExecutor();
        executor.setName("QA - tst2version");
        executor.disable();
        executorService.update(executor);

        String jsonText = ReportsTestHelper.getPlainFile(ReportType.PROTRACTOR_STEPS, 5);
        DataDTO data = new Gson().fromJson(jsonText, DataDTO.class);
        Build build = pushService.push(data);

        //Validation
        Assert.assertNull("Executor is disabled and should not be imported", build);
    }

    @After
    public void tearDown() {
        provider.clear();
    }

}
