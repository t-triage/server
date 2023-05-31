/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableAutoConfiguration
public class ProjectInfoConfig {


    private final ProjectInfoProperties properties;


    public ProjectInfoConfig(ProjectInfoProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnResource(resources = "${spring.info.build.location:classpath:META-INF/build-info.properties}")
    @ConditionalOnMissingBean
    @Bean
    public BuildProperties buildProperties() throws Exception {
        return new BuildProperties(loadFrom(this.properties.getBuild().getLocation(), "build"));
    }

    protected Properties loadFrom(Resource location, String prefix) throws IOException {
        String p = prefix.endsWith(".") ? prefix : prefix + ".";
        Properties source = PropertiesLoaderUtils.loadProperties(location);
        Properties target = new Properties();
        for (String key : source.stringPropertyNames()) {
            if (key.startsWith(p)) {
                target.put(key.substring(p.length()), source.get(key));
            }
        }
        return target;
    }
}
