package com.clarolab.api.test.view;

import com.clarolab.api.BaseAPITest;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;

public abstract class ViewServiceAPITests extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider useCaseDataProvider;

    @Before
    public void clearProvider() {
        useCaseDataProvider.clear();
    }

    @Test
    public void testViewBuildReport() {
        testUri(API_TESTCASE_URI + LIST_PATH);
    }

    @Test
    public void testTestCaseReport() {
        testUri(API_TESTCASEEPORT_URI + LIST_PATH);
    }



    @Test
    public void testViewBuildTriage() {
        String url = API_BUILDREPORT_URI + GET + "/" + useCaseDataProvider.getBuildTriage().getId();
        testUri(url);
    }

    @Test
    public void testViewTestCase() {
        String url = API_TESTCASE_URI + GET + "/" + useCaseDataProvider.getTestExecution().getId();
        testUri(url);
    }

}
