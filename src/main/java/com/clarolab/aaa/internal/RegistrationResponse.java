/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.internal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RegistrationResponse {

    private boolean success;
    private String message;

    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
