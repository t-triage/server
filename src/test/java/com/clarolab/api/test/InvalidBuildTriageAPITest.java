/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.model.BuildTriage;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildTriageService;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.ACTION_INVALIDATE_JOB;
import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static io.restassured.RestAssured.given;

public class InvalidBuildTriageAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    BuildTriageService buildTriageService;

    @Test
    public void testBasic() {
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("buildid", provider.getBuildTriage().getId())
                .post(API_ACTIONS_URI + ACTION_INVALIDATE_JOB)
                .then().statusCode(HttpStatus.SC_OK);

        BuildTriage entity = buildTriageService.find(provider.getBuildTriage().getId());

        Assert.assertTrue(!entity.isEnabled());
        Assert.assertTrue(entity.isTriaged());
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
