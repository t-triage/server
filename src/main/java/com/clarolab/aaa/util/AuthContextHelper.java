/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.util;

import com.clarolab.aaa.exception.ResourceNotFoundException;
import com.clarolab.aaa.model.UserPrincipal;
import com.clarolab.dto.UserDTO;
import com.clarolab.model.User;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.UserServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContextHelper {

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceDTO userServiceDTO;

    public User getCurrentUser() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ResourceNotFoundException("Authentication", "", "");
        }

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal == null) {
            throw new ResourceNotFoundException("Authentication user principal", "", "");
        }

        final User user = userService.find(principal.getId());

        if (user == null) {
            throw new ResourceNotFoundException("User", "id", principal.getId());
        }

        return user;
    }

    public UserDTO getCurrentUserAsDTO() {
        return userServiceDTO.convertToDTO(getCurrentUser());
    }

    public String getUsername() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ResourceNotFoundException("Authentication", "", "");
        }

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal == null) {
            return null;
        } else {
            return principal.getUsername();
        }
    }

    public String getSafeUsername() {
        String username = "";
        try {
            username = getUsername();
        } catch (ResourceNotFoundException e) {
            username = "n/a";
        }
        return username;
    }


}
