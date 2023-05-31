/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration;

import com.clarolab.QAReportApplication;
import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.impl.bamboo.BambooConnector;
import com.clarolab.connectors.impl.circleCI.CircleCIConnector;
import com.clarolab.connectors.impl.gitLab.GitLabConnector;
import com.clarolab.connectors.impl.jenkins.JenkinsConnector;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.*;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.populate.DataProvider;
import com.clarolab.runner.category.IntegrationTestCategory;
import com.clarolab.service.*;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { QAReportApplication.class})
@Category(IntegrationTestCategory.class)
@Log
public abstract class BaseIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	protected ExecutorService executorService;

	@Autowired
	protected ConnectorService connectorService;

	@Autowired
	protected ContainerService containerService;

	@Autowired
	protected TestCaseService testCaseService;

	@Autowired
	protected BuildService buildService;

	@Autowired
	protected PlatformTransactionManager transactionManager;

	@Autowired
	protected PropertyService propertyService;

	@Autowired
	protected ProductService productService;

	@Autowired
	protected CVSRepositoryService cvsRepositoryService;

	protected static Map<String, Connector> connectors = Maps.newHashMap();
	protected static Map<String, CIConnector> ciConnectors = Maps.newHashMap();
	protected static Map<String, String> urls;

	private static boolean beforeClassAlreadyConfigured = false;

	protected static int MAX_BUILDS_TO_RETRIEVE = 3;
	protected static String FOLDER_NAME = "Folder_test";
	protected static String VIEW_NAME = "Application";
	protected static String NESTEDVIEW_NAME = "view/Nested View Level 1/view/Nested View Level 2/";


	@Before
	public void init() {
		if(beforeClassAlreadyConfigured)
			return;
		log.info("*******************************************");
		log.info("Executing initializer before all test cases");
		log.info("*******************************************");

		connectors.put("JENKINS_LTHM", Connector.builder().name("JENKINS_LTHM").type(ConnectorType.JENKINS).url("http://lith-jnks-strict.phx1.jivehosted.com:8080").userName("jenkins.ci").userToken("805482bd5ba4ca42db0aad0914e84115").build());
		connectors.put("JENKINS_LIA", Connector.builder().name("JENKINS_LIA").type(ConnectorType.JENKINS).url("http://jenkins.dev.lithium.com").userName("rodrigo.rincon").userToken("4a07840fdd3fba432070d05a1125c23d").build());
		connectors.put("JENKINS_CLB", Connector.builder().name("JENKINS_CLB").type(ConnectorType.JENKINS).url("http://dev.clarolab.com:12080").userName("rodrigo_rincon").userToken("afe066d6f6086a9dcb906d21e637f3d5").build());
		connectors.put("CIRCLE_CLAROLAB", Connector.builder().name("CIRCLE_CLAROLAB").type(ConnectorType.CIRCLECI).url("https://circleci.com/").userName(StringUtils.getEmpty()).userToken("a296eed321ab07d489eae6a2eed301eac5472b19").build());
		connectors.put("GITLAB_FLUXIT", Connector.builder().name("GITLAB_FLUXIT").type(ConnectorType.GITLAB).url("https://gitlab.extranet.fluxit.com.ar").userName("nicolas.valdesogo").userToken("YXyNxc5z8P7gb69Q7YEg").build());
		connectors.put("BAMBOO_CLAROLAB", Connector.builder().name("BAMBOO_CLAROLAB").type(ConnectorType.BAMBOO).url("http://dev.clarolab.com:8085").userName("bamboo").userToken("bamboo123").build());

		refreshConnectors();

		connectors.values().forEach(connector -> {
			Connector c = connectorService.findByName(connector.getName());
			if(c == null){
				c = connectors.get(connector.getName());
				connectorService.save(c);
			}
		});

		urls = Maps.newConcurrentMap();
		urls.put("JENKINS_CLB_VIEW", "http://dev.clarolab.com:12080/view/Application/");
		urls.put("JENKINS_CLB_NESTEDVIEW", "http://dev.clarolab.com:12080/view/Nested%20View%20Level%201/view/Nested%20View%20Level%202/");
		urls.put("JENKINS_CLB_FOLDER", "http://dev.clarolab.com:12080/job/Folder_test/");
		urls.put("JENKINS_CLB_SELENIUM_VIEW", "http://dev.clarolab.com:12080/view/SeleniumTests/");
		urls.put("JENKINS_CLB_DASHBOARD", "http://dev.clarolab.com:12080/view/Dashboard_test/");
		urls.put("JENKINS_LTH_VIEW", "http://lith-jnks-strict.phx1.jivehosted.com:8080/view/Test/");
		urls.put("CIRCLE_CLAROLAB_PROJECT", "https://circleci.com/bb/TTriage/qa-reports");
		urls.put("GITLAB_FLUX_PROJECT", "https://gitlab.extranet.fluxit.com.ar/banco-galicia/back-apps");

		disabledServices();
		beforeClassAlreadyConfigured = true;
	}

	public void refreshConnectors(){
		ciConnectors.put("JENKINS_LTHM", JenkinsConnector.builder().context(initContext()).url(connectors.get("JENKINS_LTHM").getUrl()).userName(connectors.get("JENKINS_LTHM").getUserName()).passwordOrToken(connectors.get("JENKINS_LTHM").getUserToken()).build());
		ciConnectors.put("JENKINS_LIA", JenkinsConnector.builder().context(initContext()).url(connectors.get("JENKINS_LIA").getUrl()).userName(connectors.get("JENKINS_LIA").getUserName()).passwordOrToken(connectors.get("JENKINS_LIA").getUserToken()).build());
		ciConnectors.put("JENKINS_CLB", JenkinsConnector.builder().context(initContext()).url(connectors.get("JENKINS_CLB").getUrl()).userName(connectors.get("JENKINS_CLB").getUserName()).passwordOrToken(connectors.get("JENKINS_CLB").getUserToken()).build());
		ciConnectors.put("CIRCLE_CLAROLAB", CircleCIConnector.builder().context(initContext()).passwordOrToken(connectors.get("CIRCLE_CLAROLAB").getUserToken()).build());
		ciConnectors.put("GITLAB_FLUXIT", GitLabConnector.builder().context(initContext()).url(connectors.get("GITLAB_FLUXIT").getUrl()).userName(connectors.get("GITLAB_FLUXIT").getUserName()).passwordOrToken(connectors.get("GITLAB_FLUXIT").getUserToken()).build());
		ciConnectors.put("BAMBOO_CLAROLAB", BambooConnector.builder().context(initContext()).url(connectors.get("BAMBOO_CLAROLAB").getUrl()).userName(connectors.get("BAMBOO_CLAROLAB").getUserName()).passwordOrToken(connectors.get("BAMBOO_CLAROLAB").getUserToken()).build());
	}

	//@After
	public void afterTest(){
		containerService.findAll().forEach(container -> {
			if(container.getId() != null && container.getId() > 0){
				log.info(String.format("To delete container Container(id='%d', name='%s')", container.getId(), container.getName()));
				containerService.delete(container.getId());
			}
		});

//		connectorService.findAll().forEach(connector -> {
//			if(connector.getId() != null && connector.getId() > 0){
//				log.info(String.format("To delete connector Connector(id='%d', name='%s')", connector.getId(), connector.getName()));
//				connectorService.delete(connector.getId());
//			}
//		});
	}

	public void evaluateContainer(CIConnector connector, Connector con, String url) throws ContainerServiceException, ExecutorServiceException {
		log.info("To evaluate container url: " + url);
		Container container = connector.getContainer(url);
		evaluateContainer(con, container, 0);
	}

	public void evaluateContainer(CIConnector connector, Connector con, String url, int maxBuilds) throws ContainerServiceException, ExecutorServiceException {
		log.info("To evaluate container url: " + url);
		Container container = connector.getContainer(url);
		evaluateContainer(con, container, maxBuilds);
	}

	public void evaluateContainer(Connector con, Container container) throws ExecutorServiceException {
		evaluateContainer(con, container, 0);
	}

	public void evaluateContainer(Connector con, Container container, int maxBuilds) throws ExecutorServiceException {
		log.info("To evaluate container: " + container.getName());
		MatcherAssert.assertThat(container, Matchers.notNullValue());
		container.setConnector(con);
		containerService.save(container);

		CIConnector connector = container.getCIConnector();
		initContext(connector);
		connector.connect();

		List<Executor> executors;
		if(maxBuilds == 0) {
			executors = connector.getAllExecutors(container);
			validateExecutors(executors);
		}else{
			executors = connector.getAllExecutors(container, maxBuilds);
			validateExecutors(executors, maxBuilds);
		}

        log.info("Container validated successfully.");
	}

	public ApplicationContextService initContext(){
		return ApplicationContextService.builder()
				.testCaseService(testCaseService)
				.executorService(executorService)
				.transactionManager(transactionManager)
				.containerService(containerService)
				.buildService(buildService)
				.propertyService(propertyService)
				.cvsRepositoryService(cvsRepositoryService)
				.build();
	}

	public void initContext(CIConnector ciConnector){
		ciConnector.getContext().setTestCaseService(testCaseService);
		ciConnector.getContext().setExecutorService(executorService);
		ciConnector.getContext().setTransactionManager(transactionManager);
		ciConnector.getContext().setContainerService(containerService);
		ciConnector.getContext().setBuildService(buildService);
		ciConnector.getContext().setPropertyService(propertyService);
	}

	public void validateExecutors(List<Executor> executors) {
		validateExecutors(executors, 0);
	}

	public void validateExecutors(List<Executor> executors, int maxBuilds) {
		log.info("Executors: " + executors.size());
		MatcherAssert.assertThat(executors.size(), Matchers.greaterThanOrEqualTo(0));
		for(Executor e: executors) {
			List<Build> builds = e.getBuilds();
			log.info(String.format("Executor(id:%d name:'%s') --> Builds(size:%d)", e.getId(), e.getName(), builds.size()));
			if(maxBuilds == 0)
				MatcherAssert.assertThat(builds.size(), Matchers.greaterThanOrEqualTo(0));
			else
				MatcherAssert.assertThat(builds.size(), Matchers.allOf(Matchers.greaterThanOrEqualTo(0), Matchers.lessThanOrEqualTo(maxBuilds)));
			for(Build b: builds){
				MatcherAssert.assertThat(b.getReport(), Matchers.notNullValue());
				log.info(String.format("Build(id:%d number:%d) --> Tests:%d", b.getId(), b.getNumber(), b.getReport().getTotalTest()));
				if(b.getReport().getTotalTest()>0)
					MatcherAssert.assertThat(b.getReport().getTestExecutions().size(), Matchers.greaterThan(0));
			}
		}
		log.info("Executors validated successfully.");
	}

	//Utility function
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor){
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private void disabledServices(){
		log.info("Disabling Triage services to not be running.");
		TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
		List<String> props = Lists.newArrayList("TRIAGE_SERVICE_ENABLED", "POPULATE_SERVICE_ENABLED", "STATS_SERVICE_ENABLED", "EVENT_SERVICE_ENABLED");
		for(String p: props){
			Property property = propertyService.findByName(p);
			if (property == null) {
				property = DataProvider.getProperty();
				property.setName(p);
			}else if(property.getValue().toLowerCase().equals("false")){
				continue;
			}
			property.setValue("false");
			property = propertyService.save(property);
			log.info(String.format("Saved Property(id=%d, name=%s, value=%s)", property.getId(), property.getName(), property.getValue()));
		}
		transactionManager.commit(transactionStatus);
		propertyService.findAll().forEach(prop -> log.info(prop.getName() + " --> " + prop.getValue()));
	}
}
