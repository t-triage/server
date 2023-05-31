/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view.feature.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalLoginView {

    private String provider;
    private String authURI;

}
