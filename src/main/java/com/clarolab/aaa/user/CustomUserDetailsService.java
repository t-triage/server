/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.user;

import com.clarolab.aaa.exception.ResourceNotFoundException;
import com.clarolab.aaa.model.UserPrincipal;
import com.clarolab.model.User;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.model.types.RoleType;
import com.clarolab.repository.UserRepository;
import com.clarolab.service.ServiceAuthService;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServiceAuthService serviceAuthService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(email).orElse(null);
        if(user==null) {//it could be a service user
            ServiceAuth serviceAuth = serviceAuthService.findByClientId(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
            long now = DateUtils.now();
            user = User
                    .builder()
                    .enabled(false)
                    .avatar(null)
                    .timestamp(now)
                    .updated(now)
                    .agreedTermsConditions(true)
                    .lastLoginTime(now)
                    .realname("Service")
                    .roleType(RoleType.ROLE_SERVICE)
                    .username(email)
                    .password(serviceAuth.getSecretId())
                    .id(0L)
                    .build();
        }

        return UserPrincipal.create(user);
    }

    public UserDetails loadUser(User user) {
        return UserPrincipal.create(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
