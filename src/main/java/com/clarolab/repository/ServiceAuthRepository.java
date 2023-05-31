/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Connector;
import com.clarolab.model.auth.ServiceAuth;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceAuthRepository extends BaseRepository<ServiceAuth> {

    ServiceAuth findByConnector(Connector connector);

    Optional<ServiceAuth> findByClientIdIgnoreCase(String clientId);

}
