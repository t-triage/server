/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.internal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class AuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
