/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.GuideDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.model.User;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.GuideAnswer;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.GuideService;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class OnboardAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private GuideService guideService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void assign() {
        User user = provider.getUser();
        Guide guide = provider.getGuide();

        Boolean answer = given()
                .queryParam("answerType", GuideAnswer.DISMISS.getType())
                .queryParam("answer", "Ok")
                .queryParam("user", user.getId())
                .queryParam("guideid", guide.getId())
                .post(API_ONBOARDING_URI + ASSIGN)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Boolean.class);

        Assert.assertTrue(answer);
    }

    @Test
    public void list() {
        Guide first = provider.getGuide();
        provider.setGuide(null);
        Guide guide = provider.getGuide();
        guide.setPageUrl(first.getPageUrl());
        guideService.update(guide);

        // Call the service and basic checks
        TypeRef<List<GuideDTO>> responseType = new TypeRef<List<GuideDTO>>() {
        };
        List<UserDTO> items = given()
                .queryParam("page", first.getPageUrl())
                .queryParam("user", provider.getUser().getId())
                .get(API_ONBOARDING_URI + ITEMS).then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());

        Assert.assertNotNull(items);
        Assert.assertTrue(items.size() > 0);
    }

}
