/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.Executor;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.ExecutorService;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.ACTION_DISABLE_JOB;
import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static io.restassured.RestAssured.given;

public class DisableBuildTriageAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    BuildTriageService buildTriageService;

    @Autowired
    ExecutorService executorService;


    @Test
    public void testBasic() {
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);

        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());
        Executor executor = executorService.find(provider.getExecutor().getId());

        Assert.assertNotNull(entity);
        Assert.assertNotNull(executor);
        Assert.assertTrue(!executor.isEnabled());
    }

    @Test
    public void testWithNote() {
        String note = DataProvider.getRandomName("TriageNote ", 200);
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .queryParam("note", note)
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);

        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());
        Executor executor = executorService.find(provider.getExecutor().getId());

        Assert.assertNotNull(entity);
        Assert.assertNotNull(executor);
        Assert.assertTrue(!executor.isEnabled());
        Assert.assertNotNull(entity.getNote());
        Assert.assertTrue(!entity.getNote().getDescription().isEmpty());
        Assert.assertEquals(entity.getNote().getDescription(), note);
    }


    @Test
    public void testWithNoteUpdateNote() {
        String note = DataProvider.getRandomName("TriageNote ", 200);
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .queryParam("note", note)
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);

        String newNote = DataProvider.getRandomName("TriageNote ", 200);
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .queryParam("note", newNote)
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);


        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());
        Executor executor = executorService.find(provider.getExecutor().getId());

        Assert.assertNotNull(entity);
        Assert.assertNotNull(executor);
        Assert.assertTrue(!executor.isEnabled());
        Assert.assertNotNull(entity.getNote());
        Assert.assertTrue(!entity.getNote().getDescription().isEmpty());
        Assert.assertEquals(entity.getNote().getDescription(), newNote);
    }

    @Test
    public void testWithNoteDontUpdateNote() {
        String note = DataProvider.getRandomName("TriageNote ", 200);
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .queryParam("note", note)
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);

        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .post(API_ACTIONS_URI + ACTION_DISABLE_JOB)
                .then().statusCode(HttpStatus.SC_OK);


        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());
        Executor executor = executorService.find(provider.getExecutor().getId());

        Assert.assertNotNull(entity);
        Assert.assertNotNull(executor);
        Assert.assertTrue(!executor.isEnabled());
        Assert.assertNotNull(entity.getNote());
        Assert.assertTrue(!entity.getNote().getDescription().isEmpty());
        Assert.assertEquals(entity.getNote().getDescription(), note);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
