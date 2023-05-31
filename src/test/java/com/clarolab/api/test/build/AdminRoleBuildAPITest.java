package com.clarolab.api.test.build;

import com.clarolab.dto.BuildDTO;
import org.apache.http.HttpStatus;

public class AdminRoleBuildAPITest extends  BuildAPITest {

    @Override
    public void testDeleteBuild() {
        stepsDeleteBuild().then().statusCode(HttpStatus.SC_ACCEPTED);
    }

}
