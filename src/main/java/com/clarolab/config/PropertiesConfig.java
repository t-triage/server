/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config;

import com.clarolab.config.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SecurityProperties.class,
        PopulateProperties.class,
        DatasourceProperties.class,
        HibernateProperties.class,
        ApplicationConfigurationProperties.class

})
public class PropertiesConfig {}
