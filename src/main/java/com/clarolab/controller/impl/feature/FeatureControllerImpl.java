/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.feature;

import com.clarolab.controller.FeatureController;
import com.clarolab.service.UserService;
import com.clarolab.view.feature.FeatureListView;
import com.clarolab.view.feature.login.ExternalLoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.clarolab.util.Constants.API_FEATURE_URI;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(API_FEATURE_URI)
public class FeatureControllerImpl implements FeatureController {

    @Autowired
    private UserService userService;

    @Autowired
    private InMemoryClientRegistrationRepository clientRegistrationRepository;

    @Override
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureListView> getFeatures() {

        FeatureListView featureListView = new FeatureListView();

        //Login features.
        //Internal
        featureListView.setInternalLoginEnabled(userService.isInternalUserEnabled());
        //External (get every configured OAuth2 provider)
        Iterator<ClientRegistration> registrationIterator = clientRegistrationRepository.iterator();
        Set<ExternalLoginView> externalLoginViewSet = new HashSet<>();
        registrationIterator.forEachRemaining(registration ->
            externalLoginViewSet.add(new ExternalLoginView(registration.getRegistrationId(), String.format("/oauth2/authorize/%s", registration.getRegistrationId())))
        );
        featureListView.setExternalLoginURIs(externalLoginViewSet);

        //Other features...

        return ResponseEntity.ok(featureListView);
    }

}
