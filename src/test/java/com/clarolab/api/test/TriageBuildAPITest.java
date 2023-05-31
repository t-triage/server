/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.TestTriage;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.TestTriageService;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.ACTION_TRIAGED_JOB;
import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static io.restassured.RestAssured.given;

public class TriageBuildAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    BuildTriageService buildTriageService;

    @Autowired
    TestTriageService testTriageService;

    @Test
    public void testBasic() {
        provider.getTestCaseTriage();

        BuildTriageDTO buildTriage = given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .post(API_ACTIONS_URI + ACTION_TRIAGED_JOB)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(BuildTriageDTO.class);

        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());
        TestTriage test = testTriageService.find(provider.getTestCaseTriage().getId());

        Assert.assertTrue(entity.isEnabled());
        Assert.assertTrue(entity.isTriaged());
        Assert.assertTrue(test.isTriaged());
        Assert.assertTrue(test.isEnabled());
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
