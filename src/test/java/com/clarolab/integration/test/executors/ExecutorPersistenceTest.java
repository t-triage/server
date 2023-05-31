/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.executors;

import com.clarolab.integration.BaseIntegrationTest;
import com.clarolab.model.Executor;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ExecutorService;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Log
public class ExecutorPersistenceTest extends BaseIntegrationTest {

	@Autowired
	ExecutorService executorService;

	@Autowired
    ConnectorService connectorService;

//	@Test
//    public void saveContainerTest(){
//	    connectorService.save(CIConnector);
//    }
	
	@Test
	public void saveExecutorTest() {
//		try {
//
//			CIConnector.connect();
//			Executor fromJenkins = CIConnector.getExecutor(Executor.builder().name("JRF-develop-Pri1-Content-DiscussionV2-Chrome").build());
//			Executor saved = executorService.save(fromJenkins);
//			Executor find = executorService.find(saved.getId());
//			MatcherAssert.assertThat(find.getName(), Matchers.equalTo(saved.getName()));
//			MatcherAssert.assertThat(find.getBuilds().size(), Matchers.equalTo(saved.getBuilds().size()));
//			MatcherAssert.assertThat(find.getBuilds().size(), Matchers.greaterThan(0));
//			find.getBuilds().forEach( b -> log.info("Build number: "+b.getNumber()));
//			CIConnector.disconnect();
//		} catch (ConnectorServiceException e) {
//			log.info(e.getStackTrace().toString());
//		}
	}
	
	@Test
	public void findExecutorByName() {		
		Executor saved = executorService.save(Executor.builder().name("Dummy").description("This is a dummy executor").url("http://jenkins.url.com/job/Dummy").build());
		Executor find = executorService.findExecutorByName("Dummy");
		MatcherAssert.assertThat(find.getName(), Matchers.equalTo(saved.getName()));		
	}

	@Test
	public void saveExecutorWithARangeOfBuildsTest(){
//		CIConnector.connect();
//		Executor executorFromJenkins = CIConnector.getExecutor(Executor.builder().name("QA_LIA_RESPONSIVE_MEDIA").build());
//		executorService.update(executorFromJenkins);
//		executorFromJenkins = CIConnector.getExecutor(Executor.builder().name("QA_LIA_RESPONSIVE_BLOGS").build());
//		executorService.update(executorFromJenkins);
//		executorFromJenkins = CIConnector.getExecutor(Executor.builder().name("QA_LIA_RESPONSIVE_IDEAS").build());
//		executorService.update(executorFromJenkins);
////		Executor executorFromJenkins = CIConnector.getExecutor("QA_LIA_RESPONSIVE_MEDIA", "10:33");
////		Executor saved = executorService.update(executorFromJenkins);
////		saved.getBuilds().forEach( b -> log.info("Build number: "+b.getNumber()));
////		MatcherAssert.assertThat(saved.getBuilds().size(), Matchers.equalTo(24));
////		Executor updated = executorService.updateExecutor("QA_LIA_RESPONSIVE_MEDIA", CIConnector);
////		updated.getBuilds().forEach( b -> log.info("Build number: "+b.getNumber()));
////		MatcherAssert.assertThat(saved.getBuilds().size(), Matchers.greaterThan(24));
//		CIConnector.disconnect();
	}
	

}
