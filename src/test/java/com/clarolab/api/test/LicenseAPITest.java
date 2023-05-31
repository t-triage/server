package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.LicenceStatusDTO;
import com.clarolab.dto.LicenseDTO;
import com.clarolab.mapper.impl.LicenseMapper;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.LicenseService;
import com.clarolab.startup.License;
import com.clarolab.util.DateUtils;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class LicenseAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private LicenseMapper licenseMapper;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        License license = License.builder()
                .free(true)
                .licenseCode(null)
                .expirationTime(DateUtils.now())
                .build();

        LicenseDTO licenseDTO = given()
                .body(license)
                .contentType(ContentType.JSON)
                .post(API_LICENSE_URI + CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(LicenseDTO.class);

        Assert.assertNotNull(licenseDTO);
    }

    @Test
    public void testGetLicense() {
        LicenseDTO licenseDTO = given()
                .get(API_LICENSE_URI + GET)
                .then()
                .extract().as(LicenseDTO.class);

        Assert.assertNotNull(licenseDTO);
    }

    @Test
    public void testGetLicenseStatus() {
        LicenceStatusDTO licenceStatusDTO = given()
                .get(API_LICENSE_URI + STATUS)
                .then()
                .extract().as(LicenceStatusDTO.class);

        Assert.assertNotNull(licenceStatusDTO);
    }

    @Test
    public void testCheckLicenceExpiry() {
        Calendar cal = Calendar.getInstance();
        License license = licenseService.getLicense();

        Boolean answer = given()
                .get(API_LICENSE_URI + CHECK)
                .then()
                .extract().as(Boolean.class);

        Assert.assertFalse(answer);

        cal.add(Calendar.DAY_OF_YEAR, -365);
        license.setExpirationTime(cal.getTimeInMillis());
        licenseService.update(license);

        answer = given()
                .get(API_LICENSE_URI + CHECK)
                .then()
                .extract().as(Boolean.class);

        Assert.assertTrue(answer);
    }


}
