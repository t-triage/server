/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.clarolab.util.Constants.DEFAULT_SCHEDULER_POOL_SIZE;

@Configuration
public class ThreadPoolConfig {

    public static final String TRIAGE_AGENT_POOL_NAME = "triage-agent-tpool";

    @Bean
    @Qualifier(TRIAGE_AGENT_POOL_NAME)
    public ExecutorService triageAgentThreadPool() {
        return Executors.newFixedThreadPool(DEFAULT_SCHEDULER_POOL_SIZE, getNamedThreadFactory(TRIAGE_AGENT_POOL_NAME));
    }

    private static ThreadFactory getNamedThreadFactory(String prefix) {
        return new BasicThreadFactory.Builder()
                .namingPattern(prefix + "-%d")
                .priority(Thread.NORM_PRIORITY - 1)
                .build();
    }

}
