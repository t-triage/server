package com.clarolab.logtriage.repository;

import com.clarolab.logtriage.connectors.LogConnector;
import com.clarolab.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogConnectorRepository extends BaseRepository<LogConnector> {
}
