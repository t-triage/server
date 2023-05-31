/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test.export;

import com.clarolab.api.BaseAPITest;
import com.clarolab.populate.UseCaseDataProvider;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;

// Here you will find all those operations related with export service
public abstract class ExportServiceAPITest extends BaseAPITest {


    private static final String PDF = ".pdf";
    private static final String CSV = ".csv";
    private static final String XLS = ".xls";

    private static String API_EXPORT_EXECUTORS_URI= API_EXPORT_URI + EXECUTOR;
    private static String API_EXPORT_USERS_URI= API_EXPORT_URI + USER;

    @Autowired
    private UseCaseDataProvider useCaseDataProvider;


    // Tests

    @Test
    public void testExportExecutorAsPDF() {
        useCaseDataProvider.getExecutor();
        ValidatableResponse response = testUri(API_EXPORT_EXECUTORS_URI + PDF);
        response.assertThat().contentType("application/pdf");
    }

    @Test
    public void testExportExecutorAsCSV() {
        useCaseDataProvider.getExecutor();
        ValidatableResponse response = testUri(API_EXPORT_EXECUTORS_URI + CSV);
        response.assertThat().contentType("text/csv");
    }

    @Test
    public void testExportExecutorAsXLS() {
        useCaseDataProvider.getExecutor();
        ValidatableResponse response = testUri(API_EXPORT_EXECUTORS_URI + XLS);
        response.assertThat().contentType("application/vnd.ms-excel");
    }

    @Test
    public void testExportUserAsPDF() {
        ValidatableResponse response = testUri(API_EXPORT_USERS_URI + PDF);
        response.assertThat().contentType("application/pdf");
    }

    @Test
    public void testExportUserAsCSV() {
        ValidatableResponse response = testUri(API_EXPORT_USERS_URI + CSV);
        response.assertThat().contentType("text/csv");
    }

    @Test
    public void testExportUserAsXLS() {
        ValidatableResponse response = testUri(API_EXPORT_USERS_URI + XLS);
        response.assertThat().contentType("application/vnd.ms-excel");
    }


    @Before
    public void clearProvider() {
        useCaseDataProvider.clear();
    }

}
