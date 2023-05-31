/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.user;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.UserController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.UserDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.UserServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UserControllerImpl extends BaseControllerImpl<UserDTO> implements UserController {

    @Autowired
    private UserServiceDTO userService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    protected TTriageService<UserDTO> getService() {
        return userService;
    }

    @Override
    public ResponseEntity<UserDTO> currentUser() {
        return ResponseEntity.ok(userService.convertToDTO(authContextHelper.getCurrentUser()));
    }

    @Override
    public ResponseEntity<Page<UserDTO>> search(String name) {
        return ResponseEntity.ok(new PageImpl<>(userService.search(name)));
    }

    @Override
    public ResponseEntity<String> getTerm() {
        return ResponseEntity.ok(userService.getTerms());
    }
}
