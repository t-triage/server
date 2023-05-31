package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Build;
import com.clarolab.model.Executor;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private BuildService buildService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void findLastBuildOrder1() {
        Executor executor = provider.getExecutor();
        provider.getBuild(2);
        provider.setBuild(null);
        provider.getBuild(1);

        Build build = buildService.getLastBuild(executor);

        Assert.assertNotNull("The build should have been created", build);
        Assert.assertEquals("The last build is not that one", 2, build.getNumber());
    }

    @Test
    public void findLastBuildOrder2() {
        Executor executor = provider.getExecutor();
        provider.getBuild(1);
        provider.setBuild(null);
        provider.getBuild(2);

        Build build = buildService.getLastBuild(executor);

        Assert.assertNotNull("The build should have been created", build);
        Assert.assertEquals("The last build is not that one", 2, build.getNumber());
    }
}
