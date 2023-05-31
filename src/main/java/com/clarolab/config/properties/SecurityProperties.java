/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config.properties;

import com.clarolab.service.PropertyService;
import com.clarolab.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * All properties related to Login Flow and Security configurations.
 */
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    @Autowired
    private PropertyService propertyService;

    private Auth auth;

    private final OAuth2 oauth2 = new OAuth2();

    /**
     * JWT authentication specifics will be contained here.
     */
    public static class Auth {

        //Property value
        private Long configuredExpiration;

        private String tokenSecret;
        private long tokenExpirationMsec;

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenExpirationMsec() {
            //If configured use configured value
            if (configuredExpiration != null) {
                //Use configured value in property
                return configuredExpiration;
            } else {
                //If not configured check prop file.
                if (tokenExpirationMsec < 1) {
                    //Use default value.
                    return Constants.DEFAULT_JWT_AUTH_TOKEN_EXPIRATION_MS;
                } else {
                    //Use prop file value.
                    return tokenExpirationMsec;
                }
            }
        }

        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }

        Auth(Long configuredExpiration) {
            this.configuredExpiration = configuredExpiration;
        }
    }

    /**
     * Allowed redirect URIs will be contained here.
     */
    public static final class OAuth2 {

        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public void setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
        }
    }

    public Auth getAuth() {
        if (auth == null) {
            synchronized (this) {
                this.auth = new Auth(
                        //Get property value if exists.
                        propertyService.valueOf(Constants.JWT_AUTH_TOKEN_EXPIRATION_MS, (Long) null)
                );
            }
        }
        return auth;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }

}
