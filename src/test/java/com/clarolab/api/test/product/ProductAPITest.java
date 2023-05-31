/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test.product;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.ProductDTO;
import com.clarolab.model.Product;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public abstract class ProductAPITest extends BaseAPITest {

    @Autowired
    protected UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    // Abstract tests are the basic ones that should be implemented for different roles

    @Test
    public abstract void testList();

    @Test
    public abstract void testCreate();

    @Test
    public abstract void testUpdate();

    @Test
    public abstract void testDelete();

    @Test
    public abstract void testGet();

    @Test
    public abstract void testNames();


    // Steps methods

    protected Response stepsDeleteProduct(Product product) {
        return given().delete(API_PRODUCT_URI + DELETE + "/" + product.getId());
    }

    protected Response stepsCreateProduct() {
        ProductDTO productDTO = DataProvider.getProductDTO();
        return given()
                .body(productDTO)
                .contentType(ContentType.JSON)
                .post(API_PRODUCT_URI + CREATE_PATH);
    }

    protected Response stepsUpdateProduct(String newName) {
        Product product = provider.getProduct();
        ProductDTO productDTO = DataProvider.getProductDTO(product);
        productDTO.setName(newName);

        return given()
                .body(productDTO)
                .contentType(ContentType.JSON)
                .put(API_PRODUCT_URI + UPDATE_PATH);

    }

}
