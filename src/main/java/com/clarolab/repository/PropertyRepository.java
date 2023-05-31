/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Property;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends BaseRepository<Property> {

    Property findPropertiesByName(String name);

    List<Property> findAllByNameContains(String name);


}
