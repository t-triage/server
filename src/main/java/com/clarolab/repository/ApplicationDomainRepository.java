/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.ApplicationDomain;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDomainRepository extends BaseRepository<ApplicationDomain> {

    List<ApplicationDomain> findAllByAllowedAndEnabled(boolean allowed, boolean enabled);

    List<ApplicationDomain> findAllByDomainNameAndAllowedAndEnabled(String domain, boolean allowed, boolean enabled);
}
