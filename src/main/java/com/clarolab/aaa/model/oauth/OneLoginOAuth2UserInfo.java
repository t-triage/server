/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.model.oauth;

import java.util.Map;

public class OneLoginOAuth2UserInfo extends OAuth2UserInfo {

    public OneLoginOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
