/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.ApplicationDomain;
import com.clarolab.model.EnvironmentVar;
import com.clarolab.populate.DataProvider;
import com.clarolab.repository.ApplicationDomainRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.clarolab.util.Constants.URL_FRONT;

@Component
@Log
public class ApplicationDomainService extends BaseService<ApplicationDomain> {

    @Autowired
    private EnvironmentVar environmentVar;

    @Autowired
    private ApplicationDomainRepository applicationDomainRepository;

    @Autowired
    private PropertyService propertyService;

    @Override
    public BaseRepository<ApplicationDomain> getRepository() {
        return applicationDomainRepository;
    }

    public String getURL() {
        String answerURL = null;
        String envURL = environmentVar.getURL();
        String propertyURL = null;
        if (StringUtils.isEmpty(envURL) || envURL.contains("localhost") || StringUtil.countMatches(envURL, '/') < 2) {
            propertyURL = propertyService.valueOf(URL_FRONT, envURL);
        }
        if (StringUtils.isEmpty(propertyURL)) {
            answerURL = envURL;
        } else {
            answerURL = propertyURL;
        }

        if (!StringUtils.isEmpty(answerURL) && !answerURL.startsWith("http") && !answerURL.startsWith("HTTP")) {
            answerURL = "https://" + answerURL;
        }

        return answerURL;
    }

    public String getBackendURL() {
        return environmentVar.getBackendURL();
    }

    public boolean isValid(String domain) {
        if (isBlacklisted(domain)) {
            return false;
        }
        // return isWhitelisted(domain);  // disabled whitelist check

        return true;
    }

    public boolean isValidEmail(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return false;
        }
        int emailAt = userEmail.indexOf("@");
        if (emailAt > 0) {
            return isValid(userEmail.substring(emailAt + 1));
        } else {
            return false;
        }
    }

    public boolean isBlacklisted(String domain) {
        List<ApplicationDomain> domains = applicationDomainRepository.findAllByDomainNameAndAllowedAndEnabled(domain, false, true);

        return !domains.isEmpty();
    }

    public boolean isWhitelisted(String domain) {
        List<ApplicationDomain> domains = applicationDomainRepository.findAllByDomainNameAndAllowedAndEnabled(domain, true, true);

        return !domains.isEmpty();
    }

    private List<ApplicationDomain> getWhitelistDomainNames() {
        return applicationDomainRepository.findAllByAllowedAndEnabled(true, true);
    }

    private List<ApplicationDomain> blacklistDomainNames() {
        return applicationDomainRepository.findAllByAllowedAndEnabled(false, true);
    }

    public void initializeDefaultBlacklist() {
        String[] domains = {"yahoo.com", "outlook.com", "hotmail.com"};
        // String[] domains = {"google.com", "yahoo.com", "outlook.com", "hotmail.com"};
        ApplicationDomain applicationDomain;

        for (String domain : domains) {
            applicationDomain = DataProvider.getApplicationDomain();
            applicationDomain.setDomainName(domain);
            applicationDomain.setAllowed(false);

            save(applicationDomain);
        }
    }

}
