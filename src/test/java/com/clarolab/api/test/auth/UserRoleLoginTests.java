package com.clarolab.api.test.auth;

import com.clarolab.populate.DataProvider;
import org.junit.Before;

public class UserRoleLoginTests extends LoginAPITests {

    @Before
    public void setUp() {
        username = DataProvider.getEmail().toLowerCase();
        createRegularUser();
    }

}
