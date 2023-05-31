/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.integration;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.CVSLog;
import com.clarolab.model.CVSRepository;
import com.clarolab.model.TestExecution;
import com.clarolab.model.User;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.CVSRepositoryService;
import com.clarolab.service.UserService;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RepositoryFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private UserService userService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void read() {
        CVSRepository repository = provider.getCvsRepository();
        long date = DateUtils.beginDay(-25) / 1000;
        repository.setLastRead(date);
        repository.setPackageNames("com.clarolab.functional, com.clarolab.api");

        cvsRepositoryService.update(repository);

        List<CVSLog> logs = cvsRepositoryService.read(provider.getCvsRepository());

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readAndMatch() {
        CVSRepository repository = provider.getCvsRepository();

        TestTriagePopulate test = new TestTriagePopulate();
        test.setAs(StatusType.FAIL, 0, 1);
        test.setTestCaseName("read");
        test.setPath("com.clarolab.functional.test.integration.RepositoryFunctionalTest");
        TestExecution testExecution = provider.getTestExecution(test);

        provider.getBuild(1);
        provider.getBuildTriage();

        User user = provider.getUser();
        user.setUsername("francisco.vives@act-on.net");
        user.setRealname("Francisco Vives");
        userService.update(user);

        repository.setLastRead(DateUtils.beginDay(-5 * 30)/1000);
        cvsRepositoryService.update(repository);

        List<CVSLog> logs = cvsRepositoryService.read(provider.getCvsRepository());
        int count_users = 0;
        int count_tests = 0;
        for (CVSLog cvsLog : logs){
            Assert.assertNotNull(cvsLog.getTest());
            if (cvsLog.getAuthor() != null){
                count_users += 1;
            }
            if (cvsLog.getTest().equals(testExecution.getTestCase())) {
                count_tests += 1;
            }
        }
        Assert.assertTrue(count_users > 0);
        Assert.assertTrue(count_tests > 0);
        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void initConection() {
        boolean result = cvsRepositoryService.initConnection(provider.getCvsRepository());
        Assert.assertTrue(result);
    }

    @Test
    public void initConectionToken() {
        CVSRepository repository = provider.getCvsRepository();
        repository.setUsername("AAAAB3NzaC1yc2EAAAADAQABAAABgQCU4D5pQkybGHKKd0JN+nEK8zEjtrIlIRU0/8mE9zqHRUpDG82Lg8EpkJzGeY5blh0XdQoMFyybwRm4caggECs1ibzOQAteSZbhM3CLqtowqL0g4R+35Fih7SGSRQpB4E/C5Cd3E5X2uuxpE4v8iJfBr9F4XJy8iuItvCFYAODH+LojFQkgTwL5IdA6M488qpUNJmyU+9hytbXxzqQBYAxUsEY2hX7fb938xvIc238m3Zgp9e/RsZo3T9wYdh6UcaQtRWnm4Aei3J8VWaaPvM5hahNRyrSzCYOn0lhNVqZB4koGbE8GR5hYPZ81cE6BFEMraSw+gHuiun3m6hi1jqCou4u5ud8w3ykM3y8iAkad1THxmREx68XP9N4MgbsakOokZ0sD5FBuFbP76zIFWczp/js1Ad6h0CZU4fqXyrf+x65gS5GaXRMbP86ROXCcqUpcMzAcpdIp+nLMl9nRbG8sEeFi19mlwPSABe7Xtqr6BUNXfNFMXQlbEIm7uwgluMs=");
        repository.setPassword("");
        cvsRepositoryService.update(repository);

        boolean result = cvsRepositoryService.initConnection(provider.getCvsRepository());
        Assert.assertTrue(result);
    }

    @Test
    public void initConectionFailUser() {
        CVSRepository repository = provider.getCvsRepository();
        repository.setUsername("user_fail");
        cvsRepositoryService.update(repository);

        boolean result = cvsRepositoryService.initConnection(provider.getCvsRepository());
        Assert.assertFalse(result);
    }

    @Test
    public void initConectionFailPassword() {
        CVSRepository repository = provider.getCvsRepository();
        repository.setPassword("pass_fail");
        cvsRepositoryService.update(repository);

        boolean result = cvsRepositoryService.initConnection(provider.getCvsRepository());
        Assert.assertFalse(result);
    }

    @Test
    public void initConectionFailUrl() {
        CVSRepository repository = provider.getCvsRepository();
        repository.setLocalPath("localPath");
        repository.setUrl("url_fail");
        cvsRepositoryService.update(repository);

        boolean result = cvsRepositoryService.initConnection(provider.getCvsRepository());
        Assert.assertFalse(result);
    }

}
