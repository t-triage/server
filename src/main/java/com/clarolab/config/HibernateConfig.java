/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config;

import com.clarolab.config.properties.DatasourceProperties;
import com.clarolab.model.EnvironmentVar;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Autowired
    private DatasourceProperties properties;

    @Autowired
    private EnvironmentVar environmentVar;


    @Bean
    public DataSource dataSource() {
        if (properties.getPlatform().equals("h2") || environmentVar.getDBUrl() == null) {
            return dataSourceFromProperty();
        } else {
            return dataSourceFromEnvironment();
        }
    }

    public DataSource dataSourceFromEnvironment() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(environmentVar.getDBDriverClassName());
        dataSource.setJdbcUrl(environmentVar.getDBUrl());
        dataSource.setUsername(environmentVar.getDBUsername());
        dataSource.setPassword(environmentVar.getDBPassword());
        dataSource.setMaximumPoolSize(environmentVar.getMaximumPoolSize());
        dataSource.setMaxLifetime(environmentVar.getMaxLifetime()); //ten min
        return dataSource;
    }


    public DataSource dataSourceFromProperty() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setMaximumPoolSize(properties.getMaximumPoolSize());
        dataSource.setMaxLifetime(properties.getMaxLifetime()); //ten min
        return dataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.clarolab");
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }


}
