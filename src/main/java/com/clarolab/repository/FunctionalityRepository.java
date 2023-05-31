/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;


import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.ManualTestCase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionalityRepository extends BaseRepository<Functionality> {

    List<ManualTestCase> findAllByEnabled(boolean enabled);

    Functionality findFirstByNameIgnoreCaseAndEnabledOrderByIdDesc(String name, boolean enabled);
    
    @Query("SELECT f FROM Functionality f WHERE LOWER(f.name) LIKE ?1 AND f.enabled = ?2 ORDER BY f.name")
    List<Functionality> searchFunctionality(String name, boolean enabled);
    
    Functionality findFirstByExternalIdIgnoreCaseAndEnabledOrderByIdDesc(String externalId, boolean enabled);

}
