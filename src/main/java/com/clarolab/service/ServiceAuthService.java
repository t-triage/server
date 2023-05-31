/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Connector;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ServiceAuthRepository;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
public class ServiceAuthService extends BaseService<ServiceAuth> {

    @Autowired
    private ServiceAuthRepository serviceAuthRepository;

    @Override
    public BaseRepository<ServiceAuth> getRepository() {
        return serviceAuthRepository;
    }

    private PasswordEncoder passwordEncoder;

    public ServiceAuth findByConnector(Connector connector){
        return serviceAuthRepository.findByConnector(connector);
    }

    public ServiceAuth getServiceAuth(Connector connector) {
        return findByConnector(connector);
    }

    public ServiceAuth newServiceAuth(Connector connector) {
        final String secretID = StringUtils.generateSecretID();

        ServiceAuth serviceAuth = findByConnector(connector);

        if(serviceAuth==null){

            String clientID;
            ServiceAuth auth;

            do {
                clientID = StringUtils.generateClientID();
                auth = findByClientId(clientID).orElse(null);
            } while (auth != null);


            serviceAuth = ServiceAuth.builder()
                    .connector(connector)
                    .clientId(clientID)
                    .secretId(passwordEncoder.encode(secretID))
                    .build();
            save(serviceAuth);
        }
        else {
            serviceAuth.setSecretId(passwordEncoder.encode(secretID));
            update(serviceAuth);
        }
        serviceAuth.setSecretId(secretID);
        return serviceAuth;
    }

    public Optional<ServiceAuth> findByClientId(String clientID) {
        return serviceAuthRepository.findByClientIdIgnoreCase(clientID);
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
