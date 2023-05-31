/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.filter;

import com.clarolab.QAReportApplication;
import com.clarolab.aaa.util.SecurityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * If the chain got here without any auth it means it has to be un-authorized.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Autowired
    private SecurityLog securityLogger;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        logger.error("URL - {}", httpServletRequest.getRequestURL());
        logger.error("Unauthorized error. Message - {}", e.getMessage());
        getSecurityLogger().error(String.format("Unauthorized error. URL %s. Message - %s", httpServletRequest.getRequestURL(), e.getMessage()));
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getLocalizedMessage());
    }

    private SecurityLog getSecurityLogger() {
        if (securityLogger == null) {
            ApplicationContext applicationContext = QAReportApplication.getApplicationContext();
            if (applicationContext != null) {
                securityLogger = applicationContext.getBean(SecurityLog.class);
            }
            if (securityLogger == null) {
                securityLogger = new SecurityLog();
            }
        }

        return securityLogger;
    }
}
