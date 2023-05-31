package com.clarolab.api.test.build;

import org.apache.http.HttpStatus;
import org.junit.Before;

public class UserRoleBuildAPITest extends BuildAPITest {

    @Before
    public void setUp() {
        regularUserSetUp();
    }


    @Override
    public void testDeleteBuild() {
        stepsDeleteBuild().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

}
