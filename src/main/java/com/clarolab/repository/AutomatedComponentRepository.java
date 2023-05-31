/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.component.AutomatedComponent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomatedComponentRepository extends BaseRepository<AutomatedComponent> {

    List<AutomatedComponent> findAllByEnabled(boolean enabled);

    @Query("SELECT c FROM AutomatedComponent c WHERE LOWER (c.name) LIKE ?1 AND c.enabled = TRUE ORDER BY c.name")
    List<AutomatedComponent> search(String name, boolean enabled);

}
