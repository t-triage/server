/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.RestPageImpl;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.model.Container;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ContainerService;
import com.clarolab.service.TriageSpecService;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.API_CONTAINER_URI;
import static com.clarolab.util.Constants.SUGGESTED_URI;
import static io.restassured.RestAssured.given;

public class ContainerSuggestionAPITest extends BaseAPITest {

    private static String API_URL = API_CONTAINER_URI + SUGGESTED_URI;

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private ContainerService containerService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void baseEmptyTest() {
        testUri(API_URL);
    }

    @Test
    public void withoutSuggestionAmount() {

        int initialContainerAmount = containerService.findAllEnabled().size();
        // Prepare Data
        int amount = 10;
        createContainers("z", amount);

        // Call the service and basic checks
        TypeRef<RestPageImpl<ContainerDTO>> responseType = new TypeRef<RestPageImpl<ContainerDTO>>() {
        };
        RestPageImpl<ContainerDTO> suggestedContainers = given()
                .get(API_URL)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());
        Assert.assertNotNull(suggestedContainers.getContent());
        Assert.assertFalse(suggestedContainers.isEmpty());
        List<ContainerDTO> answer = suggestedContainers.getContent();

        // Asserts
        Assert.assertEquals(amount + initialContainerAmount, answer.size());
    }

    @Test
    public void suggestions() {
        // Prepare Data
        int initialContainerAmount = containerService.findAllEnabled().size();
        int amount = 5;

        // Create loggedUserContainers: Priority 1
        provider.setName("Sugz1");
        provider.setUser(getLoggedUser());
        createContainer();
        provider.getTriageSpec().setPriority(1);
        triageSpecService.update(provider.getTriageSpec());
        Container first = provider.getContainer();

        // Create loggedUserContainers: Priority 2
        provider.setName("Sugz2");
        createContainer();
        provider.getTriageSpec().setPriority(1);
        triageSpecService.update(provider.getTriageSpec());
        Container second = provider.getContainer();

        // Create Others
        provider.setUser(null);
        List<Container> otherContainers = createContainers("A", amount);


        // Call the service and basic checks
        TypeRef<RestPageImpl<ContainerDTO>> responseType = new TypeRef<RestPageImpl<ContainerDTO>>() {
        };
        RestPageImpl<ContainerDTO> suggestedContainers = given()
                .get(API_URL)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());
        Assert.assertNotNull(suggestedContainers.getContent());
        Assert.assertFalse(suggestedContainers.isEmpty());
        List<ContainerDTO> answer = suggestedContainers.getContent();

        // Asserts
        Assert.assertEquals(initialContainerAmount + amount + 2, answer.size());

        Assert.assertEquals(first.getName(), answer.get(0).getName());
        Assert.assertEquals(second.getName(), answer.get(1).getName());
        for (int i = 0; i < amount; i++) {
            // must keep the alphabetic order
            Assert.assertEquals(String.format("Container sort not correct at: %d", i), otherContainers.get(i).getName(), answer.get(2 + i).getName());
        }
    }

    private List<Container> createContainers(String prefix, int amount) {
        provider.clear();
        List<Container> answer = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            provider.setName(prefix + String.valueOf(i));
            createContainer();
            answer.add(provider.getContainer());
        }
        return answer;
    }

    private void createContainer() {
        provider.setContainer(null);
        provider.setTriageSpec(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.getTestCaseTriage();
    }

}
