package com.clarolab.aaa.util;

import com.clarolab.QAReportApplication;
import com.clarolab.util.Constants;
import com.clarolab.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

// Audit security actions and put in security-logger.log

@Service
public class SecurityLog {
    private static final Logger logger = LoggerFactory.getLogger(Constants.SECURITY_LOG);

    @Autowired
    private AuthContextHelper authContextHelper;

    public void info(String log) {
        logger.info(format(log));
    }

    public void warn(String log) {
        logger.warn(format(log));
    }

    public void error(String log) {
        logger.error(format(log));
    }

    public void error(String log, Throwable exception) {
        logger.error(format(log), exception);
    }

    private String format(String log) {
        if (log == null) {
            return "";
        }

        String ip = MDC.get(Constants.USER_IP);

        if (StringUtils.isEmpty(ip)) {
            ip = "noip";
        }

        String user = getUsername();
        if (user == null) {
            user = "-";
        }

        return String.format("%s - %s: %s",ip, user, log);
    }

    private String getUsername() {
        String username = MDC.get(Constants.USER_USERNAME);
        if (!StringUtils.isEmpty(username)) {
            return username;
        }
        if (authContextHelper == null) {
            ApplicationContext applicationContext = QAReportApplication.getApplicationContext();
            if (applicationContext != null) {
                authContextHelper = applicationContext.getBean(AuthContextHelper.class);
            }
            if (authContextHelper == null) {
                return null;
            }
        }
        return authContextHelper.getSafeUsername();
    }
}
