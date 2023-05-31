package com.clarolab.integration.test.reports;

import com.clarolab.QAReportApplication;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.impl.utils.report.builder.CypressReportBuilder;
import com.clarolab.connectors.impl.utils.report.builder.PythonReportBuilder;
import com.clarolab.connectors.impl.utils.report.cypress.json.entity.MainCypress;
import com.clarolab.connectors.impl.utils.report.python.json.entity.MainPython;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
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

import java.util.ArrayList;

@Log
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {QAReportApplication.class})
@Category(IntegrationTestCategory.class)
public class PythonReportTest extends BaseReportTest {


    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ReportService reportService;

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

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void reportPythonTest() {

        for (String file : pythonFiles) {
            log.info("Testing python file: " + file);
            String reportStr = super.getReportContentFromJson(super.getReport("python", file));

            MainPython mainPython = PythonReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainPython.class);

            Report report = Report.builder()
                    .type(ReportType.PYTHON)
                    .status(mainPython.getStatus())
                    .passCount(mainPython.getPassed())
                    .failCount(mainPython.getFailed())
                    .warningCount(mainPython.getWarning())
                    .duration(mainPython.getDuration())
                    .executiondate(0L)
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();

            report.add(mainPython.getTests(true));

            MatcherAssert.assertThat(report, Matchers.notNullValue());
        }
    }

    @Test
    public void reportUtilPythonTest() {
        String file = "reports/python/python1.json";
        log.info("Testing python file: " + file);
        String content = super.getReportContentFromJson(super.getReport("python", file));
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);

        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view");
        dataDTO1.setJobName("aurora-smoke-suite");
        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(213);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName("python-report.json");
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        Report report = ReportUtils.builder().context(context).applicationTestingEnvironmentVersion(version).cvsLogs("").build().getReportData(dataDTO1);

        Report dbReport = reportService.save(report);

        MatcherAssert.assertThat(report, Matchers.notNullValue());
        MatcherAssert.assertThat(dbReport, Matchers.notNullValue());
    }


    @Test
    public void pushPythonTest() {
        provider.getExecutor();
        Container container = provider.getContainer();
        container.setName("aurora-smoke-suite-view");
        containerService.update(container);

        String file = "reports/cypress/python1.json";
        log.info("Testing cypress file: " + file);
        String content = super.getReportContentFromJson(super.getReport("python", file));
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);

        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view");
        dataDTO1.setJobName("aurora-smoke-suite");
        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(213);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName("python-report.json");
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        pushService.push(dataDTO1);

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

