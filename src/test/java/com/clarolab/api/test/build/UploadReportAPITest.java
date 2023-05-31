package com.clarolab.api.test.build;

import com.clarolab.api.BaseAPITest;
import com.clarolab.model.types.ReportType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.populate.util.ReportsTestHelper;
import com.clarolab.view.KeyValuePair;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_EXECUTOR_URI;
import static com.clarolab.util.Constants.PUSH_PATH;
import static io.restassured.RestAssured.given;

public class UploadReportAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testUploadJunit() {
        KeyValuePair message = given()
                .queryParam("executorid", (long) provider.getExecutor().getId())
                .queryParam("reportType", ReportType.JUNIT)
                .body(ReportsTestHelper.getReportContentFromXml(ReportType.JUNIT))
                .post(API_EXECUTOR_URI + PUSH_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(KeyValuePair.class);

        Assert.assertNotNull(message);
    }
}
