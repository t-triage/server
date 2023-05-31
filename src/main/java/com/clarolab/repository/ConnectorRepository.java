/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Connector;
import com.clarolab.model.types.ConnectorType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectorRepository extends BaseRepository<Connector> {

    List<Connector> findAllByName(String name);

    Connector findByName(String name);

    List<Connector> findAllByType(ConnectorType type);

}
