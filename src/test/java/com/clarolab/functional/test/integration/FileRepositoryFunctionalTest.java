/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.integration;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.CVSLog;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestExecution;
import com.clarolab.model.User;
import com.clarolab.model.Product;
import com.clarolab.model.types.LogType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.CVSRepositoryService;
import com.clarolab.service.LogService;
import com.clarolab.service.UserService;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class FileRepositoryFunctionalTest extends BaseFunctionalTest {

    protected static String CSV_RESOURCES_PATH = "cvs/";

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test
    public void readStatMercurial() {
        //hg log -T xml
        String fileContent = getFileContent("mercurialDevCode.hg");

        Product product = provider.getProduct();
        product.setPackageNames("com.clarolab.functional.test");

        List<CVSLog> logs = cvsRepositoryService.read(fileContent, product, LogType.MERCURIAL);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readStatSVN() {
        //svn log --xml --verbose
        String fileContent = getFileContent("svnJunitsTriage.svn");

        Product product = provider.getProduct();
        product.setPackageNames("com.clarolab.functional.test");

        List<CVSLog> logs = cvsRepositoryService.read(fileContent, product, LogType.SVN);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readStat() {
        // git log --since="4 week ago" --numstat --source --pretty=format:"%h | %an | %ae | %ai | %cn | %ce | %ci | %s"
        String filecontent = getFileContent("seleniumTriage.log");

        Product product = provider.getProduct();
        product.setPackageNames("com.clarolab");

        List<CVSLog> logs = cvsRepositoryService.read(filecontent, product, LogType.GIT);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readComplete() {
        String filecontent = getFileContent("gitDevCode.log");

        Product product = provider.getProduct();
        product.setPackageNames("test.src.test");

        List<CVSLog> logs = cvsRepositoryService.read(filecontent, product, LogType.GIT);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readSeleniumMineraloil() {
        String filecontent = getFileContent("gitSeleniumMineraloil.log");

        TestCase test = new TestCase();
        test.setLocationPath("responsive.cwaBlog.versionCompare.AbstractVersionCompare");
        test.setName("test");
        testCaseService.save(test);

        User user = new User();
        user.setRealname("vivekmishra-khoros");
        user.setUsername("57979349+vivekmishra-khoros@users.noreply.github.com");
        userService.save(user);

        Product product = provider.getProduct();
        product.setPackageNames("com.lithium.mineraloil.lia.test.responsive.cwaBlog");

        List<CVSLog> logs = cvsRepositoryService.readAndProcess(filecontent, product, LogType.GIT);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void readAndMatch() {
        String filecontent = getFileContent("junitsTriage.log");

        TestTriagePopulate test = new TestTriagePopulate();
        test.setAs(StatusType.FAIL, 0, 1);
        test.setTestCaseName("read");
        test.setPath("com.clarolab.functional.test.integration.FileRepositoryFunctionalTest");
        TestExecution testExecution = provider.getTestExecution(test);

        Product product = new Product();
        product.setPackageNames("com.clarolab");
        product.setDescription("Test product!");
        productService.save(product);

        provider.getBuild(1);
        provider.getBuildTriage();

        User user = provider.getUser();
        user.setUsername("francisco.vives@act-on.net");
        user.setRealname("Francisco Vives");
        userService.update(user);

        List<CVSLog> logs = cvsRepositoryService.readAndProcess(filecontent, product, LogType.GIT);

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    private String getFileContent(String filename) {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(CSV_RESOURCES_PATH + filename);
        String fileContent = "";
        try {
            fileContent = IOUtils.toString(fileStream, Charset.defaultCharset());
        } catch (IOException e) {
            Assert.fail("Couldn't read file: " + filename);
        }

        return fileContent;
    }


}
