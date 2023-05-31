/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;


@SpringBootApplication
@EnableScheduling
@EnableCaching
public class QAReportApplication {

    private static ApplicationContext applicationContext;

    private static boolean started;

    private static final Logger log = LoggerFactory.getLogger(QAReportApplication.class);

    public static void main(String[] args) throws UnknownHostException {

//        if (!LicenceValidator.isValid()) return;

        SpringApplication app = new SpringApplication(QAReportApplication.class);

        applicationContext = app.run(args);
        Environment env = applicationContext.getEnvironment();

        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
        started = true;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        QAReportApplication.applicationContext = applicationContext;
    }

    public static boolean isStarted(){
        return QAReportApplication.started;
    }

    @Bean
    public CacheManager getCacheManager() {
        return new NoOpCacheManager();
    }

}