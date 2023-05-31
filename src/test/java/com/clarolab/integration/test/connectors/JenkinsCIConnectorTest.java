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
import com.clarolab.runner.category.JenkinsCIConnectorCategory;
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

import static org.hamcrest.Matchers.is;

@Log
@Category(JenkinsCIConnectorCategory.class)
public class JenkinsCIConnectorTest extends BaseIntegrationTest {

	@Ignore
	@Test
	@Rollback(false)
	public void verifyConnection() {
		CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		MatcherAssert.assertThat(connector.isConnected(), Matchers.is(true));
	}

	@Test
	@Rollback(false)
	public void verifyDisconnection() {
		CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		connector.disconnect();
		MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
		refreshConnectors();
	}

	@Test(expected = ContainerServiceException.class)
	@Rollback(false)
	public void getViewFromNameTest() throws ContainerServiceException {
		CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		Container container = connector.getContainer(VIEW_NAME);
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container = connector.getContainer("/view/Application");
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container = connector.getContainer("view/Application");
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container = connector.getContainer("/view/Application/job/multi");
		MatcherAssert.assertThat(container, Matchers.nullValue());
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getExecutorsInViewTest() throws ExecutorServiceException, ContainerServiceException {
		CIConnector connector;
		Connector c = connectorService.findByName("JENKINS_CLB");
		if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

		Container container = containerService.findByName(VIEW_NAME);
		if(container == null){
			connector = ciConnectors.get("JENKINS_CLB").connect();
			container = connector.getContainer("http://dev.clarolab.com:12080/view/Application");
			container.setConnector(c);
			container = containerService.save(container);
		}

		connector = CIConnector.getConnector(container).connect();
		initContext(connector);

		List<Executor> executors = connector.getAllExecutors(container);
		MatcherAssert.assertThat(executors.isEmpty(), is(false));
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getExecutorInfoTest() throws ContainerServiceException, ExecutorServiceException {
		CIConnector connector;
		Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

		Container container = containerService.findByName(VIEW_NAME);
		if(container == null){
			connector = ciConnectors.get("JENKINS_CLB").connect();
			container = connector.getContainer(VIEW_NAME);
			container.setConnector(c);
			containerService.save(container);
		}

		connector = CIConnector.getConnector(container).connect();
		initContext(connector);

		List<Executor> executors = connector.getAllExecutors(container);
		validateExecutors(executors);
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getViewsTest() throws ContainerServiceException {
		CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();

		List<Container> containerList = connector.getAllContainers();
		MatcherAssert.assertThat(containerList.size(), Matchers.greaterThan(0));
		containerList.forEach(container -> {
			MatcherAssert.assertThat(container.getName(), Matchers.notNullValue());
			MatcherAssert.assertThat(container.getUrl(), Matchers.notNullValue());
			log.info(String.format( "Container info: Container(name='%s' , url='%s')", container.getName(), container.getUrl()));
		});
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getExecutorsOnJenkinsFolderTest() throws ExecutorServiceException, ContainerServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();

		Container folder = connector.getContainer(FOLDER_NAME);
		MatcherAssert.assertThat(folder, Matchers.notNullValue());
		folder.setConnector(c);
		containerService.save(folder);

		connector = folder.getCIConnector();
		initContext(connector);
		connector.connect();

		List<Executor> executors = connector.getAllExecutors(folder, MAX_BUILDS_TO_RETRIEVE);
		executors.forEach(executor -> log.info("Executor: " + executor.getName()));
		validateExecutors(executors);
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getExecutorsOnJenkinsViewTest() throws ExecutorServiceException, ContainerServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();

		Container container = connector.getContainer(VIEW_NAME);
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container.setConnector(c);
		containerService.save(container);

		connector = container.getCIConnector();
		initContext(connector);
		connector.connect();

		List<Executor> executors = connector.getAllExecutors(container, MAX_BUILDS_TO_RETRIEVE);
		MatcherAssert.assertThat(executors.size(), Matchers.greaterThan(0));
		validateExecutors(executors);
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getExecutorsOnJenkinsNestedViewTest() throws ExecutorServiceException, ContainerServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();

		Container container = connector.getContainer(NESTEDVIEW_NAME);
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container.setConnector(c);
		containerService.save(container);

		connector = container.getCIConnector();
		initContext(connector);
		connector.connect();

		List<Executor> executors = connector.getAllExecutors(container);
		MatcherAssert.assertThat(executors.size(), Matchers.greaterThanOrEqualTo(0));
		validateExecutors(executors);
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getViewFromURLTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		evaluateContainer(connector, c, urls.get("JENKINS_CLB_VIEW"));
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getNestedViewFromURLTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		evaluateContainer(connector, c, urls.get("JENKINS_CLB_NESTEDVIEW"));
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getFolderFromURLTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		evaluateContainer(connector, c, urls.get("JENKINS_CLB_FOLDER"));
	}

	@Ignore
	@Test
	@Rollback(false)
	public void getDashboardFromURLTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		evaluateContainer(connector, c, urls.get("JENKINS_CLB_DASHBOARD"));
	}

	//@Test
	//@Rollback(false)
	public void checkExecutorBuilds() throws ContainerServiceException, BuildServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("JENKINS_CLB");
        if(c == null){
            c = connectors.get("JENKINS_CLB");
            c = connectorService.save(c);
        }

        CIConnector connector = ciConnectors.get("JENKINS_CLB").connect();
		evaluateContainer(connector, c, urls.get("JENKINS_CLB_SELENIUM_VIEW"), 5);
		Executor seleniumTests = executorService.findExecutorByName("TTriage Test Selenium Chrome");
		connector.getContext().setExecutorToContext(seleniumTests);

		List<Build> builds = buildService.findAll(seleniumTests);
		builds.forEach(b -> log.info("Show build info Build(id="+b.getId()+" number="+b.getNumber()+")"));
		MatcherAssert.assertThat(builds.size(), Matchers.lessThanOrEqualTo(5));
		TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		for(int i = 0; i<3; i++){
			buildService.delete(builds.get(0).getId());
			builds.remove(0);
		}
		transactionManager.commit(transactionStatus);
		builds = buildService.findAll(seleniumTests);
		builds.forEach(b -> log.info("Show build info Build(id="+b.getId()+" number="+b.getNumber()+")"));
		int seleniumTestsLatestBuildOnDB = seleniumTests.getLastExecutedBuild().getNumber();
		int seleniumTestslatestBuildOnCI = connector.getExecutorLatestBuild(seleniumTests);
		log.info(String.format("For executor '%s' was found as last executed build: #%d on database and #%d on CI tool.",seleniumTests.getName(), seleniumTestsLatestBuildOnDB, seleniumTestslatestBuildOnCI));
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

	@Ignore
	@Test
	public void populateTest() throws ContainerServiceException {
		Product tProduct = Product.builder()
				.name("t-Triage")
				.description("Real-time insights on software automation")
				.enabled(true)
				.packageNames("com.clarolab")
				.build();
		productService.save(tProduct);

		Connector c = connectorService.findByName("JENKINS_LTHM");
		if(c == null){
			c = connectors.get("JENKINS_LTHM");
			c = connectorService.save(c);
		}
		CIConnector connector = ciConnectors.get("JENKINS_LTHM").connect();
		Container container = connector.getContainer(urls.get("JENKINS_LTH_VIEW"));
		container.setConnector(c);
		container.setProduct(tProduct);
		containerService.save(container);
		connector = container.getCIConnector();
		initContext(connector);
		connector.connect();

		containerService.populate(containerService.findByName(container.getName()).getId());
		int sizeExecutor = executorService.findAll().size();
		int sizeBuild = buildService.findAll().size();

		MatcherAssert.assertThat(sizeExecutor, Matchers.greaterThan(0));
		MatcherAssert.assertThat(sizeBuild, Matchers.greaterThan(0));
		refreshConnectors();
	}

	private boolean buildIsGreaterThan(List<Build> builds, Build build){
		return builds.stream().noneMatch(b -> b.getNumber() > build.getNumber());
	}

}
