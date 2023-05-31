/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.login;

import com.clarolab.aaa.internal.ClientSecret;
import com.clarolab.aaa.internal.LoginRequest;
import com.clarolab.aaa.internal.RegistrationRequest;
import com.clarolab.controller.LoginController;
import com.clarolab.model.User;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.service.PropertyService;
import com.clarolab.service.ServiceAuthService;
import com.clarolab.service.UserService;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.clarolab.util.Constants.*;

@RestController
@CrossOrigin
@RequestMapping(value = AUTH)
public class LoginControllerProxy implements LoginController {

    @Autowired
    @Qualifier("login-controller-impl")
    private LoginController loginController;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceAuthService serviceAuth;

    @Override
    @PostMapping(value = LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@Validated @RequestBody LoginRequest loginRequest) {

        if (!userService.isInternalUserEnabled())
            return ResponseEntity.notFound().build();

        User user = userService.findByUsername(loginRequest.getEmail());
        if(user == null || !user.isEnabled())
            return ResponseEntity.notFound().build();

        user.setLastLoginTime(DateUtils.now());
        userService.update(user);
        return loginController.authenticate(loginRequest);
    }

    @Override
    @PostMapping(value = TOKEN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@Validated @RequestBody ClientSecret loginRequest) {
        if (!propertyService.valueOf(SERVICEL_LOGIN_ENABLED, DEFAULT_SERVICE_LOGIN_ENABLED))
            return ResponseEntity.notFound().build();

        ServiceAuth auth = serviceAuth.findByClientId(loginRequest.getClientId()).orElse(null);
        if(auth == null || !auth.isEnabled())
            return ResponseEntity.notFound().build();
        return loginController.authenticate(loginRequest);
    }

    @Override
    @PostMapping(value = SIGNUP, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Validated @RequestBody RegistrationRequest registrationRequest) {

        if (!userService.isInternalUserEnabled()) {
            return ResponseEntity.notFound().build();
        }

        return loginController.register(registrationRequest);
    }
}
