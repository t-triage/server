package com.clarolab.functional.test.model;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.LicenseService;
import com.clarolab.service.TestTriageService;
import com.clarolab.service.UserService;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.startup.License;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class LicenceFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private LicenceValidator licenceValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private StaticRuleDispatcher staticRuleDispatcher;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        long date = DateUtils.now();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, +1);
        long expStamp = cal.getTimeInMillis();

        License entity = License.builder()
                .creationTime(date)
                .expirationTime(expStamp)
                .expired(false)
                .licenseCode("j2JxHD0xnPk0wOI3J37t1k4yiSD5epHTWDOi$XvvrvItfEEZWGyCyuiTcspJtSHI6PY7NJQFdli50nRTzIGnvVwXflA==")
                .free(false)
                .build();

        entity = licenseService.save(entity);

    }

    @Test
    public void checkLicenseExpiration() {
        License license = License.builder()
                .creationTime(DateUtils.now())
                .expirationTime(DateUtils.beginDay(1))
                .expired(false)
                .licenseCode("j2JxHD0xnPk0wOI3J37t1k4yiSD5epHTWDOi$XvvrvItfEEZWGyCyuiTcspJtSHI6PY7NJQFdli50nRTzIGnvVwXflA==")
                .free(false)
                .build();

        boolean isExpired = licenceValidator.isExpired(license);
        Assert.assertFalse(isExpired);

        license.setExpirationTime(DateUtils.offSetDays(-400));

        isExpired = licenceValidator.isExpired(license);
        Assert.assertTrue(isExpired);
    }

    @Test
    public void testUserCreation() {
        License license = licenseService.getLicense();
        license.setFree(true);
        licenseService.update(license);

        Assert.assertTrue(licenceValidator.validateUserCreation());

        for (int x = 0; x < 6; ++x) {
            try {
                provider.getUser();
            } catch (ConfigurationError e) {
                Assert.assertEquals(5, userService.countEnabled());
            }
            clearProvider();
        }
    }

    @Test
    public void testManualTestCreation() {
        License license = licenseService.getLicense();
        license.setFree(true);
        licenseService.update(license);

        Assert.assertTrue(licenceValidator.validateTestCreation());

        for (int x = 0; x < 51; ++x) {
            try {
                provider.getManualTestCase(1);
                provider.setManualTestCase(null);
            } catch (ConfigurationError e) {
                Assert.assertEquals(50, manualTestCaseService.countEnabled());
            }
        }
    }

    @Test
    public void testTestTriaged() {
        License license = licenseService.getLicense();
        license.setEnabled(true);
        license.setFree(false);
        licenseService.update(license);

        provider.getTrendGoal();

        Assert.assertTrue(licenceValidator.validateTestTriaged());

        Build build = provider.getBuild();
        TriageSpec spec = provider.getTriageSpec();
        BuildTriage buildTriage = provider.getBuildTriage();

        license.setFree(true);
        licenseService.update(license);

        for (int x = 0; x < 301; ++x) {
            provider.setTestExecution(null);
            TestExecution testCase = provider.getTestExecution();

            TestTriage testTriage = staticRuleDispatcher.process(testCase, build, spec);

            testTriage.setBuildTriage(buildTriage);
            testTriage.initialize();

            try {
                testTriage = testTriageService.save(testTriage);

                if (x <= 100) {
                    testTriage.setTimestamp(DateUtils.offSetDays(-10));
                    testTriageService.update(testTriage);
                }

            } catch (ConfigurationError error) {
                Assert.assertEquals(200, testTriageService.countByEnabledToday());
            }

            Assert.assertTrue(testTriageService.countByEnabledToday() <= 200);
        }

    }

}
