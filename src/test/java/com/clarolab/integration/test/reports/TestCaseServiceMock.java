package com.clarolab.integration.test.reports;

import com.clarolab.model.TestCase;
import com.clarolab.service.TestCaseService;

public class TestCaseServiceMock extends TestCaseService {

    public TestCase testCaseLike(TestCase test) {
        return test;
    }
}
