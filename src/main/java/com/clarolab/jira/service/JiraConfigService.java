/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.jira.service;

import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.repository.JiraConfigRepository;
import com.clarolab.model.*;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ProductRepository;
import com.clarolab.service.BaseService;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.NotificationService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.JIRA_TOKEN_MONTH_NOTIFICATION;

@Service
public class JiraConfigService extends BaseService<JiraConfig> {

    @Autowired
    private JiraConfigRepository jiraConfigRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private BuildTriageService buildTriageService;
    @Autowired
    private UserService userService;

    private Map<String, JiraConfig> cache = new HashMap<>();
    @Override
    public BaseRepository<JiraConfig> getRepository() {
        return jiraConfigRepository;
    }

    public JiraConfig findByProduct(Product product){
        return jiraConfigRepository.findByProduct(product);
    }

    public JiraConfig findLastByProductId(Long product){
        return jiraConfigRepository.findByProductIdAndEnabledTrue(product);
    }

    public JiraConfig findByProjectKey(String key){
        return jiraConfigRepository.findByProjectKey(key);
    }

    public JiraConfig findByProductcache(Product product) {
        JiraConfig jiraConfig;
        JiraConfig cacheValue = findCache(product.getName());
        if (cacheValue != null) {
            return cacheValue;
        }
        jiraConfig = jiraConfigRepository.findByProduct(product);
        if(jiraConfig == null){
            return null;
        }
        updateCache(product.getName(), jiraConfig);
        return jiraConfig;
    }
    private JiraConfig findCache(String name) {
        boolean enabled = true;
        String propertyName = name.toLowerCase();

        if (enabled) {
            return cache.get(propertyName);
        } else {
            return null;
        }

    }
    private void updateCache(String name, Entry entity) {
        String propertyName = name.toLowerCase();

        cache.put(propertyName, (JiraConfig) entity);
    }

    public void notifyUserTokenExpired(JiraConfig jiraConfig){
        Set<User> users = buildTriageService.findAllByContainerProductAndUpdatedGreatedThanTwoMonth(jiraConfig.getProduct())
                .stream()
                .map(BuildTriage::getTriager)
                .collect(Collectors.toSet());
        Calendar twoMonthAgo = Calendar.getInstance();
        twoMonthAgo.add(Calendar.MONTH, JIRA_TOKEN_MONTH_NOTIFICATION);
        List<Notification> notifications = notificationService.createNotificationIfUnseen(
                "Jira Connection is expired",
                "Your Jira Token for " + jiraConfig.getProduct().getName() + " is expired, please reconnect.",
                1,
                new ArrayList<>(users),
                Calendar.getInstance().getTimeInMillis()-twoMonthAgo.getTimeInMillis()
                );
    }
}
