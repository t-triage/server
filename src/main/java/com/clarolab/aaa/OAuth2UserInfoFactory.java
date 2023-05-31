/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa;

import com.clarolab.aaa.exception.OAuth2AuthenticationProcessingException;
import com.clarolab.aaa.model.oauth.GoogleOAuth2UserInfo;
import com.clarolab.aaa.model.oauth.OAuth2UserInfo;
import com.clarolab.aaa.model.oauth.OktaOAuth2UserInfo;
import com.clarolab.aaa.model.oauth.OneLoginOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        // Google User
        if(registrationId.equalsIgnoreCase(AuthenticationProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);

        // OneLogin User
        } else if (registrationId.equalsIgnoreCase(AuthenticationProvider.onelogin.toString())) {
            return new OneLoginOAuth2UserInfo(attributes);
        // Okta User
        } else if (registrationId.equalsIgnoreCase(AuthenticationProvider.okta.toString())){
            return  new OktaOAuth2UserInfo(attributes);

        // If we got here means no provider configured.
        } else {
            throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        }
    }
}
