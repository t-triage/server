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
public class ClientSecret {

    @NotBlank
    @Email
    private String clientId;
    @NotBlank
    private String secretId;
}
