package com.clarolab.logtriage.service;

import com.clarolab.logtriage.connectors.LogConnector;
import com.clarolab.logtriage.repository.LogConnectorRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogConnectorService extends BaseService<LogConnector> {

    @Autowired
    private LogConnectorRepository repository;

    @Override
    protected BaseRepository<LogConnector> getRepository() {
        return this.repository;
    }
}
