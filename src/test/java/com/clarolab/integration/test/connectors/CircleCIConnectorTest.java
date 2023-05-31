/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.connectors;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.integration.BaseIntegrationTest;
import com.clarolab.model.Build;
import com.clarolab.model.Connector;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.runner.category.CircleCIConnectorCategory;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Log
@Category(CircleCIConnectorCategory.class)
@Rollback(false)
public class CircleCIConnectorTest extends BaseIntegrationTest {

    @Test
    public void checkVPN() {
        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(true));

    }

    @Test
    public void verifyDisconnectionTest() {
        CIConnector connector =  ciConnectors.get("CIRCLE_CLAROLAB").connect();
        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Test
    public void getContainersTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        List<Container> containers = connector.getAllContainers();
        log.info("Size: " + containers.size());
        containers.forEach(container -> log.info(container.getName()
                + " ; "
                + container.getHiddenData()[0]
                + " ; "
                + container.getHiddenData()[1]
                + " ; "
                + container.getHiddenData()[2]
                + " ; "
                + container.getUrl()
                + " ; "
                + container.getDescription()
        ));
        MatcherAssert.assertThat(containers.size(), Matchers.greaterThan(0));
    }

    @Test
    public void getContainerTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        Container container = connector.getContainer("qa-reports");
        log.info(container==null ?"NULL" : container.getName()
                + " ; "
                + container.getHiddenData()[0]
                + " ; "
                + container.getHiddenData()[1]
                + " ; "
                + container.getHiddenData()[2]
                + " ; "
                + container.getUrl()
                + " ; "
                + container.getDescription()
        );
        MatcherAssert.assertThat(container, Matchers.notNullValue());
    }

    @Test
    public void getContainerFromURLTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("CIRCLE_CLAROLAB");
        if(c == null){
            c = connectors.get("CIRCLE_CLAROLAB");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        evaluateContainer(connector, c, urls.get("CIRCLE_CLAROLAB_PROJECT"));

    }

    @Test
    public void avoidDuplicatedDataPopulatingTwice() throws ContainerServiceException {
        Product tProduct = Product.builder()
                .name("t-Triage")
                .description("Real-time insights on software automation")
                .enabled(true)
                .packageNames("com.clarolab")
                .build();
        productService.save(tProduct);

        Connector c = connectorService.findByName("CIRCLE_CLAROLAB");
        if(c == null){
            c = connectors.get("CIRCLE_CLAROLAB");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        Container container = connector.getContainer(urls.get("CIRCLE_CLAROLAB_PROJECT"));
        container.setConnector(c);
        container.setProduct(tProduct);
        containerService.save(container);
        connector = container.getCIConnector();
        initContext(connector);
        connector.connect();

        containerService.populate(containerService.findByName(container.getName()).getId());
        int sizeExecutor = executorService.findAll().size();
        int sizeBuild = buildService.findAll().size();

        containerService.populate(containerService.findByName(container.getName()).getId());
        MatcherAssert.assertThat(executorService.findAll().size(), Matchers.is(sizeExecutor));
        MatcherAssert.assertThat(buildService.findAll().size(), Matchers.is(sizeBuild));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void checkExecutorBuilds() throws ContainerServiceException, BuildServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("CIRCLE_CLAROLAB");
        if(c == null){
            c = connectors.get("CIRCLE_CLAROLAB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("CIRCLE_CLAROLAB").connect();
        evaluateContainer(connector, c, urls.get("CIRCLE_CLAROLAB_PROJECT"), 5);
        Executor circleJob = executorService.findExecutorByName("test");
        connector.getContext().setExecutorToContext(circleJob);

        List<Build> builds = buildService.findAll(circleJob);
        builds.forEach(b -> log.info("Show build info Build(id="+b.getId()+" number="+b.getNumber()+")"));
        MatcherAssert.assertThat(builds.size(), Matchers.lessThanOrEqualTo(5));
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        for(int i = 0; i<3; i++){
            buildService.delete(builds.get(0).getId());
            builds.remove(0);
        }
        transactionManager.commit(transactionStatus);
        builds = buildService.findAll(circleJob);
        builds.forEach(b -> log.info("Show build info Build(id="+b.getId()+" number="+b.getNumber()+")"));
        int seleniumTestsLatestBuildOnDB = circleJob.getLastExecutedBuild().getNumber();
        int seleniumTestslatestBuildOnCI = connector.getExecutorLatestBuild(circleJob);
        log.info(String.format("For executor '%s' was found as last executed build: #%d on database and #%d on CI tool.",circleJob.getName(), seleniumTestsLatestBuildOnDB, seleniumTestslatestBuildOnCI));
//		List<Build> recovered = null;
//		if (simulateLatestBuildOnDB < latestBuildOnCI) {
//			recovered = connector.getExecutorBuilds(simulatedExecutorOnDB, 5);
//			recovered = recovered.stream().filter(build -> build.getNumber() > simulateLatestBuildOnDB).collect(Collectors.toList());
//			MatcherAssert.assertThat(recovered.size(), Matchers.lessThanOrEqualTo(5));
//			recovered.forEach(b -> MatcherAssert.assertThat(buildIsGreaterThan(builds, b), Matchers.is(true)));
//			//MatcherAssert.assertThat(builds.size(), is(Ints.checkedCast(recovered.stream().filter(distinctByKey(element -> element.getBuildId())).count())));
//		}
//		executorService.save(simulatedExecutorOnDB);
    }
}
