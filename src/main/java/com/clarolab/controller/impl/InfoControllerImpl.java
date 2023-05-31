/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.InfoController;
import com.clarolab.jira.model.JiraObject;
import com.clarolab.jira.service.JiraOAuthService;
import com.clarolab.model.Property;
import com.clarolab.service.PropertyService;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.clarolab.util.Constants.GOOGLE_ANALYTICS_UA;
import static com.clarolab.util.Constants.WELCOME_MESSAGE;

@CrossOrigin
@RestController
public class InfoControllerImpl implements InfoController {

    // @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private JiraOAuthService jiraOAuthService;

    @Override
    public ResponseEntity<List<KeyValuePair>> getBuildInfo() {
        return getBuildInfoWithAnnotation();
    }

    public ResponseEntity<List<KeyValuePair>> getBuildInfoDummy() {
        List<KeyValuePair> values = Lists.newArrayList();
        KeyValuePair number = KeyValuePair.builder().key("build.number").value("331").build();
        KeyValuePair name = KeyValuePair.builder().key("build.name").value("332").build();
        KeyValuePair date = KeyValuePair.builder().key("build.date").value("333").build();
        KeyValuePair time = KeyValuePair.builder().key("build.time").value("334").build();
        KeyValuePair copyright = KeyValuePair.builder().key("build.copyright").value("335").build();
        KeyValuePair version = KeyValuePair.builder().key("build.version").value("337").build();

        values.add(name);
        values.add(number);
        values.add(date);
        values.add(time);
        values.add(copyright);
        values.add(version);

        return ResponseEntity.ok(values);
    }

    public ResponseEntity<List<KeyValuePair>> getBuildInfoWithAnnotation() {
        List<KeyValuePair> values = Lists.newArrayList();
        if (KeyValuePair.builder().key("build.number") == null || buildProperties == null || buildProperties.get("number") == null ||
                KeyValuePair.builder().key("build.number").value(buildProperties.get("number").replace(",","")) == null) {
            return getBuildInfoDummy();
        }
        KeyValuePair number = KeyValuePair.builder().key("build.number").value(buildProperties.get("number").replace(",","")).build();
        KeyValuePair name = KeyValuePair.builder().key("build.name").value(buildProperties.get("name")).build();
        KeyValuePair date = KeyValuePair.builder().key("build.date").value(buildProperties.get("date")).build();
        KeyValuePair time = KeyValuePair.builder().key("build.time").value(buildProperties.get("time")).build();
        KeyValuePair copyright = KeyValuePair.builder().key("build.copyright").value(buildProperties.get("copyright")).build();
        KeyValuePair version = KeyValuePair.builder().key("build.version").value(buildProperties.get("version")).build();

        values.add(name);
        values.add(number);
        values.add(date);
        values.add(time);
        values.add(copyright);
        values.add(version);

        return ResponseEntity.ok(values);
    }

    @Override
    public ResponseEntity<String> getWelcomeMessage() {
        Property property = propertyService.findByName(WELCOME_MESSAGE);
        if (property == null) {
            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.ok(property.getValue());
        }
    }

    @Override
    public ResponseEntity<String> getGoogleAnalyticsUA() {
        Property property = propertyService.findByName(GOOGLE_ANALYTICS_UA);
        if (property == null) {
            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.ok(property.getValue());
        }
    }

    @Override
    public ResponseEntity<String> health() {
        Property property = propertyService.findByName(GOOGLE_ANALYTICS_UA);
        if (property == null) {
            return ResponseEntity.ok("");
        } else {
            return ResponseEntity.ok("OK");
        }
    }

    @Override
    public ResponseEntity<JiraObject>  getRefreshCode(String code, Long productId){
        return ResponseEntity.ok(jiraOAuthService.getRefreshCode(code, productId));
    }
}
