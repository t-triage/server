package com.clarolab.api.test.product;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ProductDTO;
import com.clarolab.model.Product;
import org.apache.http.HttpStatus;
import org.junit.Assert;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class AdminRoleProductAPITest extends ProductAPITest {

    @Override
    public void testList() {
        testUri(API_PRODUCT_URI + LIST_PATH);
    }

    @Override
    public void testCreate() {
        stepsCreateProduct()
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(ProductDTO.class);
    }

    @Override
    public void testUpdate() {
        String newName = "NewName";
        ProductDTO save = stepsUpdateProduct(newName)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(ProductDTO.class);

        Assert.assertEquals(newName, save.getName());
    }

    @Override
    public void testDelete() {
        Product product = provider.getProduct();
        stepsDeleteProduct(product).then().statusCode(HttpStatus.SC_ACCEPTED);

        ErrorInfo result = given()
                .expect()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .when()
                .get(API_BUILD_URI + GET + "/" + product.getId()).as(ErrorInfo.class);

        Assert.assertEquals(result.getCode(), HttpStatus.SC_NOT_FOUND);
        Assert.assertEquals(result.getError(), "Not Found");
    }

    @Override
    public void testGet() {
        String url = API_PRODUCT_URI + GET + "/" + provider.getProduct().getId();
        testUri(ProductDTO.class, url);
    }

    @Override
    public void testNames() {
        testUri(API_PRODUCT_URI + NAMES);
    }

}
