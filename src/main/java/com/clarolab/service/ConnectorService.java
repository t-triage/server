/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.connectors.CIConnector;
import com.clarolab.model.Connector;
import com.clarolab.model.Container;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ConnectorRepository;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

import static com.clarolab.util.Constants.DEFAULT_POPULATE_DELAY;
import static com.clarolab.util.Constants.DEFAULT_POPULATE_FREQUENCY;
import static com.clarolab.util.StringUtils.getSystemError;

@Service
@Log
public class ConnectorService extends BaseService<Connector> {

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ServiceAuthService serviceAuthService;

    @Autowired
    private PropertyService propertyService;

    @Override
    public BaseRepository<Connector> getRepository() {
        return connectorRepository;
    }

    public Connector findByName(String name) {
        return connectorRepository.findByName(name);
    }

    @Scheduled(fixedRate = DEFAULT_POPULATE_FREQUENCY, initialDelay = DEFAULT_POPULATE_DELAY)
    // @Scheduled(cron = START_DAY_JOB_CRON)
    public boolean populateAll() {
        propertyService.warmUp();
        if (!(propertyService.valueOf("POPULATE_SERVICE_ENABLED", true)))
            return false;
        log.info("Starting to process all connectors.");
        Instant start = DateUtils.instantNow();

        // List<Connector> connectors = connectorToPopulateAll();
        List<Long> containerIds = containerService.findContainersToPopulate();
        log.info(String.format("Processing all %d containers", containerIds.size()));
        
        containerIds.stream().forEach(containerId ->
                containerService.populateAtomic(containerId)
        );

        log.info(String.format("All %s containers processed in %s seconds", containerIds.size(), DateUtils.getElapsedTime(start, DateUtils.instantNow())));

        return true;

    }

    public boolean populate(Long id) {
        return populate(find(id));
    }

    public boolean populate(final Connector connector) {
        return populate(connector.getContainers());
    }

    public boolean populate() {
        return populate(containerService.findAll());
    }

    public List<Connector> connectorToPopulateAll() {
        return findAll();
    }

    private boolean populate(List<Container> containers) {
       /* containers.forEach(container ->
                containerService.populate(container.getId())
        );*/

        containers.stream().forEach(container ->
                containerService.populateAtomic(container.getId())
        );

        return true;
    }

    public List<Container> getAllContainers(Long id) {
        return getAllContainers(find(id));
    }

    public List<Container> getAllContainers(Connector connector) {
        CIConnector ciConnector = connector.getCIConnector();
        return containerService.getAllContainers(ciConnector);
    }

    public List<Container> getAllContainers() {
        List<Connector> connectors = findAll();
        List<Container> containers = Lists.newArrayList();
        connectors.forEach(connector -> containers.addAll(getAllContainers(connector)));
        return containers;
    }

    public List<Connector> findAll(ConnectorType type) {
        return connectorRepository.findAllByType(type);
    }

    public List<Connector> findAll(String name) {
        return connectorRepository.findAllByName(name);
    }

    public Boolean isValid(Long id) {
        boolean connected = false;

        try {
            Connector connector = find(id);
            CIConnector ciConnector = connector.getCIConnector();
            ciConnector.connect();
            connected = ciConnector.isConnected();
            ciConnector.disconnect();
        } catch (Exception e) {
            String msg = String.format("Could not connect to Connector(id=%d)", id);
            log.severe(getSystemError(msg));
            log.log(Level.INFO, msg);
        }
        return connected;
    }

}
