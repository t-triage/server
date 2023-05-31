/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.QAReportApplication;
import com.clarolab.connectors.impl.utils.report.builder.ProtractorReportBuilder;
import com.clarolab.connectors.impl.utils.report.builder.ProtractorV2ReportBuilder;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.MainProtractor;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.MainProtractorV2;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.runner.category.IntegrationTestCategory;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.JsonUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.InputStream;
import java.util.ArrayList;

@Log
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {QAReportApplication.class})
@Category(IntegrationTestCategory.class)
public class ProtractorReportTest extends BaseReportTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private PushService pushService;

    @Test
    public void reportProtractorTest(){

        for(String file: protractorFiles){
            log.info("Testing protractor file: " + file);
            String reportStr = super.getReportContentFromXml(super.getReport("protractor", file));

            MainProtractor mainProtractor = ProtractorReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainProtractor.class);

            Report report =  Report.builder()
                    .type(ReportType.PROTRACTOR)
                    .status(mainProtractor.getStatus())
                    .executiondate(0L)
                    .passCount(mainProtractor.getPassed())
                    .failCount(mainProtractor.getFailed())
                    .skipCount(mainProtractor.getSkipped())
                    .duration(mainProtractor.getDuration())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainProtractor.getTests(true));

            MatcherAssert.assertThat(report, Matchers.notNullValue());
            //MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }

    @Test
    public void reportProtractorV2Test(){

        for(String file: protractorV2Files){
            log.info("Testing protractor file: " + file);
            String reportStr = super.getReportContentFromXml(super.getReport("protractorv2", file));

            MainProtractorV2 mainProtractorV2 = ProtractorV2ReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainProtractorV2.class);

            Report report =  Report.builder()
                    .type(ReportType.PROTRACTOR)
                    .status(mainProtractorV2.getStatus())
                    .executiondate(0L)
                    .passCount(mainProtractorV2.getPassed())
                    .failCount(mainProtractorV2.getFailed())
                    .skipCount(mainProtractorV2.getSkipped())
                    .duration(mainProtractorV2.getDuration())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainProtractorV2.getTestCase(true));

            MatcherAssert.assertThat(report, Matchers.notNullValue());
            //MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }
    @Test
    public void pushTest() {
        provider.getExecutor();
        Container container = provider.getContainer();
        container.setName("Protractor tests");
        containerService.update(container);
        String file = "reports/protractor/protractor3.xml";
        String file3="reports/protractor/protractor4.xml";
        String file2= "reports/protractor/protractor5.xml";
        containerService.update(container);
        provider.setContainer(container);
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(file);
        InputStream inputStream2 = RealDataProvider.class.getClassLoader().getResourceAsStream(file2);
        InputStream inputStream3 = RealDataProvider.class.getClassLoader().getResourceAsStream(file3);
        String content = getReportContentFromXml(inputStream);
        String content2= getReportContentFromXml(inputStream2);
        String content3= getReportContentFromXml(inputStream3);
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        DataDTO dataDTO = new DataDTO();
        dataDTO.setArtifacts(new ArrayList<>());
        dataDTO.setViewName("SANDBOX");
        dataDTO.setJobName("AfterFix2");
        dataDTO.setJobId(0l);
        dataDTO.setBuildNumber(213);
        dataDTO.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        dataDTO.setBuildStatus("FAILURE");
        dataDTO.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");
        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName("report-ProtractorOutput.xml");
        // artifact.setFileType("xml");
        artifact.setFileType("xml");
        dataDTO.getArtifacts().add(artifact);
        ArtifactDTO artifact2 = new ArtifactDTO();
        artifact2.setContent(content2);
        artifact2.setFileName("report-ProtractorOutput.xml");
        // artifact.setFileType("xml");
        artifact2.setFileType("xml");
        dataDTO.getArtifacts().add(artifact2);
        ArtifactDTO artifact3 = new ArtifactDTO();
        artifact3.setContent(content3);
        artifact3.setFileName("report-ProtractorOutput.xml");
        // artifact.setFileType("xml");
        artifact3.setFileType("xml");
        dataDTO.getArtifacts().add(artifact3);
        pushService.push(dataDTO);

    }
    private ApplicationContextService getActualAppContext(Executor executor) {
        return ApplicationContextService
                .builder()
                .product(executor.getContainer().getProduct())
                .executorService(executorService)
                .buildService(buildService)
                .testCaseService(testCaseService)
                .propertyService(propertyService)
                .containerService(containerService)
                .transactionManager(transactionManager)
                .container(executor.getContainer())
                .build();
    }
}
