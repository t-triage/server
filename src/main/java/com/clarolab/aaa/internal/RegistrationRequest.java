/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.internal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RegistrationRequest {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;

}
