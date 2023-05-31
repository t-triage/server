/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.login;

import com.clarolab.aaa.AuthenticationProvider;
import com.clarolab.aaa.internal.*;
import com.clarolab.aaa.jwt.TokenProvider;
import com.clarolab.controller.LoginController;
import com.clarolab.model.User;
import com.clarolab.service.UserService;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.clarolab.util.Constants.API_USER_URI;
import static com.clarolab.util.Constants.ME;

@Component
@Qualifier("login-controller-impl")
public class LoginControllerImpl implements LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public ResponseEntity<?> authenticate(ClientSecret clientLoginRequest) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(clientLoginRequest.getClientId());
        loginRequest.setPassword(clientLoginRequest.getSecretId());
        return authenticate(loginRequest);

    }

    public ResponseEntity<?> authenticate(LoginRequest loginRequest) {
        cacheUsername(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        getParsedEmail(loginRequest.getEmail()),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    public ResponseEntity<?> register(RegistrationRequest registrationRequest) {

        // Creating user's account
        long timestamp = DateUtils.now();
        User user = new User();
        user.setTimestamp(timestamp);
        user.setUpdated(timestamp);
        user.setRealname(registrationRequest.getName());
        user.setUsername(getParsedEmail(registrationRequest.getEmail()));
        user.setProvider(AuthenticationProvider.internal);

        //Put raw password service will encode
        user.setPassword(userService.getEncryptedPassword(registrationRequest.getPassword()));

        User result = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path(API_USER_URI + ME)
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(new RegistrationResponse(true, "User registered successfully."));
    }

    private String getParsedEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        } else {
            return email.trim();
        }
    }

    private void cacheUsername(LoginRequest loginRequest) {
        if (loginRequest == null) {
            return;
        }
        if (!StringUtils.isEmpty(loginRequest.getEmail())) {
            MDC.put(Constants.USER_USERNAME, loginRequest.getEmail());
        }
    }
}
