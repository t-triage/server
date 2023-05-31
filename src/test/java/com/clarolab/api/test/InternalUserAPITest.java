/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.ProductDTO;
import com.clarolab.model.Property;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ProductService;
import com.clarolab.service.PropertyService;
import com.clarolab.service.UserService;
import com.clarolab.util.Credentials;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class InternalUserAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductService productService;

    @Before
    public void disableInternalUser() {
        setEnableInternalUser(false);
    }

    @After
    public void enableInternal() {
        setEnableInternalUser(true);
    }

    protected void initialize() {
        initializeRestAssured();
        provider.clearContainer();
        disableInternalUser();
    }

    @Test
    public void apiWithInternalUserDisabled() {
        ProductDTO productDTO = DataProvider.getProductDTO();
        productDTO.setName(DataProvider.getRandomName("apiWithInternalUserDisabled"));
        String productName = productDTO.getName();

        ProductDTO productAnswered = given()
                .body(productDTO)
                .contentType(ContentType.JSON)
                .post(API_PRODUCT_URI + CREATE_PATH)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract()
                .as(ProductDTO.class);

        boolean found = productService.findAll()
                .stream()
                .anyMatch(product -> product.getName().equals(productName));

        Assert.assertFalse("Product should have not been created at the DB since internal user is disabled", found);
    }

    @Test
    public void cantAuthenticate() {
        Response response = null;
        Credentials credentials = new Credentials();
        credentials.setEmail(System.getProperty("qe.user"));
        credentials.setPassword(System.getProperty("qe.pass"));

        try {
             response = given()
                            .accept(ContentType.JSON)
                            .contentType(ContentType.JSON)
                            .body(credentials)
                            .expect()
                            .statusCode(HttpStatus.SC_NOT_FOUND)
                            .when()
                            .post(AUTH + LOGIN);
        } catch (Exception ex) {
            // it is ok, it should have explote
            Assert.assertTrue("API with internal user should produce a Connection Refused but got: " + ex.getLocalizedMessage(), ex.getLocalizedMessage().contains("Connection refused"));
            return;
        }

        Assert.assertEquals("The api call shouldn't have authentication body", "", response.body().print());
    }

    private void setEnableInternalUser(boolean enabled) {
        Property internalUserProperty = propertyService.findByName(INTERNAL_LOGIN_ENABLED);
        if (internalUserProperty == null) {
            internalUserProperty = DataProvider.getProperty();
            internalUserProperty.setName(INTERNAL_LOGIN_ENABLED);
            internalUserProperty.setValue(String.valueOf(enabled));

            internalUserProperty = propertyService.save(internalUserProperty);
        }
        internalUserProperty.setValue(String.valueOf(enabled));

        propertyService.update(internalUserProperty);
    }

}
