package com.clarolab.api.test.product;

import org.apache.http.HttpStatus;
import org.junit.Before;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class UserRoleProductAPITest extends ProductAPITest {

    @Before
    public void setUp() {
        regularUserSetUp();
    }

    @Override
    public void testList() {
        given()
                .get(API_PRODUCT_URI + LIST_PATH)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testCreate() {
        stepsCreateProduct().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testUpdate() {
        stepsUpdateProduct("NewName").then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testDelete() {
        stepsDeleteProduct(provider.getProduct()).then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testGet() {
        given()
                .get(API_PRODUCT_URI + GET + "/" + provider.getProduct().getId())
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testNames() {
        given()
                .get(API_PRODUCT_URI + NAMES)
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

}
