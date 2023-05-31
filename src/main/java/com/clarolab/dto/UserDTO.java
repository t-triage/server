/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO {

    protected String username;
    private String realname;
    private String displayName;
    private String password;
    private String avatar;
    private String roleType;
    private String provider;
    private boolean agreedTermsConditions;
    private long timestampTermsConditions;
    private boolean internal;
}
