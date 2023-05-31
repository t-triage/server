package com.clarolab.service;


import com.clarolab.dto.LicenceStatusDTO;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.LicenseRepository;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.startup.License;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LicenseUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Log
public class LicenseService extends BaseService<License> {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private LicenceValidator licenseValidator;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private NotificationService notificationService;

    @Override
    protected BaseRepository<License> getRepository() {
        return licenseRepository;
    }

    public License getLicense() {
        License license = licenseRepository.findTopByEnabledIsTrue();

        if (license == null) {
            license = License.builder()
                    .free(true)
                    .expired(true)
                    .expirationTime(DateUtils.offSetDays(-365))
                    .licenseCode(null)
                    .build();
        }

        return license;
    }

    @Override
    public License save(License entry) {
        Calendar cal = Calendar.getInstance();

        if (!entry.isFree()) {
            List<String> decodedArgs = LicenseUtils.licenceDecode(entry.getLicenseCode());
            if (licenseValidator.validateLicenceDomain(decodedArgs.get(0))) {
                cal.add(Calendar.MONTH, Integer.parseInt(decodedArgs.get(1)));
            } else {
                log.info("License not valid...");
                throw new ConfigurationError("Your license is invalid, please try again.");
            }
        } else {
            cal.add(Calendar.YEAR, 1);
        }

        entry.setExpirationTime(cal.getTimeInMillis());
        entry.setCreationTime(DateUtils.now());

        disableExpiredLicense();

        License license = super.save(entry);

        if (license.isFree())
            notificationService.createNotification("Free version",
                    "You have a free license of t-Triage. For a commercial version, please contact support.",
                    0,
                    userService.getAllAdminUser());
        else
            notificationService.createNotification("Congratulations!",
                    "You have activated a full version of t-Triage.",
                    0,
                    userService.getAllAdminUser());

        return license;
    }

    public int disableExpiredLicense() {
        return licenseRepository.disableExpiredLicense();
    }


    public LicenceStatusDTO getLicenseStatus() {

        License preexistingLicense = this.getLicense();
        LicenceStatusDTO answer = new LicenceStatusDTO();

        long maxUsers = 5;
        long maxTests = 50;
        long maxTriagedTests = 200;

        long currentUsers = userService.countEnabled();
        long currentsManualTests = manualTestCaseService.countEnabled();
        long currentTriagedTests = testTriageService.countByEnabledToday();

        boolean valid = licenseValidator.isExpired(preexistingLicense);

        answer.setUsersLeft(maxUsers - currentUsers);
        answer.setManualTestsLeft(maxTests - currentsManualTests);
        answer.setTriagedTestsLeft(maxTriagedTests - currentTriagedTests);
        answer.setExpired(valid);

        return answer;


    }

    // Working with cache

    @CachePut("licence")
    public boolean checkLicenseExpiry() {

        License preexistingLicense = this.getLicense();

        return licenseValidator.isExpired(preexistingLicense);

    }

    @Scheduled(cron = "0 0 0/12 * * ?")
    public void addCacheJob() {
        checkLicenseExpiry();
        System.out.println("Cache saved" + new Date());

    }


}
