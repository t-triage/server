/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.dto.ContainerItemDTO;
import com.clarolab.model.*;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ContainerRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.clarolab.util.Constants.DEFAULT_MAX_BUILDS_TO_PROCESS;
import static com.clarolab.util.StringUtils.getSystemError;

@Service
@Log
public class ContainerService extends BaseService<Container> {

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ProductService productService;

    @Override
    public BaseRepository<Container> getRepository() {
        return containerRepository;
    }

    public boolean populate(String name) {
        Container container = findByName(name);
        if (container == null) return false;
        return populate(container.getId());
    }

    public Container findByName(String name) {
        return containerRepository.findByName(name);
    }

    public Container findByName(Product product, String name) {
        return containerRepository.findByProductAndNameLike(product, name);
    }

    public boolean populateAtomic(Long id) {
        Long lock = new Long(id);
        synchronized (lock) {
            try {
                return populate(id);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Could not populate container", ex);
            }
        }
        return false;
    }

    public boolean populate(Long id) {
        Instant startTime = DateUtils.instantNow();
        Container container = find(id);
        if (!container.isHierarchicalyEnabled()) {
            log.info("Container(name=" + container.getName() + ") is disabled");
            return false;
        }
        if (container.isPushMode()) {
            log.info("Skipping Container(name=" + container.getName() + ") due to it's in population mode " + container.getPopulateMode().name());
            return false;
        }
        log.info("To populate Container(name=" + container.getName() + ")");

        CIConnector ciconnector = CIConnector.getConnector(container).connect();
        if (ciconnector == null) {
            log.log(Level.SEVERE, String.format("Could not connect to Connector(id=%d) to perform a pull", container.getConnector().getId()));
            return false;
        }
        executorService.buildContext(ciconnector.getContext());
        if (!executorService.checkIfThereAreByContainerAndEnabled(container, true)) {
            log.info("Container is being populated for first time.");
            // This is the first time that populate is running. So container does not has any executor.
            try {
                //Executor *--> Build --> Report are saved on "getAllExecutors" method
                ciconnector.getAllExecutors(container, DEFAULT_MAX_BUILDS_TO_PROCESS);
                testCaseService.cleanNewTests();
                // assureTestUniqueness(newExecutors);
                // this.update(container);
            } catch (ExecutorServiceException ex) {
                log.log(Level.SEVERE, String.format("Could not pull data from Container(id=%d , name=%s)", container.getId(), container.getName()), ex);
                ciconnector.disconnect();
                return false;
            }
        } else {
            //Update executors that already exist on DB
            List<Executor> executorList = executorService.findAllByContainerAndEnabled(container, true);
            executorList.forEach(executor -> {
                try {
                    log.info("Executor(name=" + executor.getName() + ") should be updated.");
                    executorService.populate(executor, ciconnector);
                } catch (ExecutorServiceException ex) {
                    log.log(Level.SEVERE, String.format("Could not pull data from Executor(id=%d , name=%s)", executor.getId(), executor.getName()), ex);
                }
            });

            //New executors on CI, and not on DB
            try {
                log.log(Level.INFO, String.format("Check new executors for Container(id=%d , name=%s)", container.getId(), container.getName()));
                List<Executor> newExecutors = ciconnector.checkForNewExecutors(container, DEFAULT_MAX_BUILDS_TO_PROCESS);
                if (CollectionUtils.isNotEmpty(newExecutors)) {
                    assureTestUniqueness(newExecutors);
                    container.add(newExecutors);
                    containerRepository.save(container);
                } else {
                    log.log(Level.INFO, String.format("There are no new executors for Container(id=%d , name=%s)", container.getId(), container.getName()));
                }
            } catch (ExecutorServiceException ex) {
                log.log(Level.SEVERE, String.format("Error while check for new executors on Container(id=%d , name=%s)", container.getId(), container.getName()), ex);
            }

        }

        ciconnector.disconnect();
        postPopulate();
        log.info(String.format("Populate for Container(name=%s) has been completed successfully in %s", container.getName(), DateUtils.getElapsedTime(startTime, DateUtils.instantNow())));
        return true;
    }

    public List<Container> getAllContainers(CIConnector connector) {
        connector.connect();
        List<Container> allContainers = Lists.newArrayList();
        try {
            allContainers = connector.getAllContainers();
        } catch (ContainerServiceException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        connector.disconnect();
        return allContainers;
    }

    public List<Long> getAllContainerIds() {
        return containerRepository.findAllContainerIds();
    }

    public List<Container> findAllEnabled() {
        List<Container> list = containerRepository.findTop50ByEnabledOrderByName(true);
        List<Container> answer = new ArrayList<>(list.size());

        for (Container container : list) {
            if (container.isActive()) {
                answer.add(container);
            }
        }
        return answer;
    }

    public List<Container> containersFrom(List<Long> executorsIds) {
        if (executorsIds.isEmpty()) return Lists.newArrayList();

        return containerRepository.findAllByExecutorIn(executorsIds);
    }

    public List<Container> suggested(User user) {
        long tomorrow = DateUtils.beginDay(1);

        // It shows tomorrow list first since the today list will be in the previous section
        List<Container> tomorrowList = containerRepository.findAllByUserFrom(user, tomorrow);
        List<Container> todayList = containerRepository.findAllByUserUntil(user, tomorrow);
        List<Container> allContainers = findAllEnabled();

        ArrayList<Container> answer = new ArrayList<>(allContainers.size());

        collectContainers(tomorrowList, answer);
        collectContainers(todayList, answer);
        collectContainers(allContainers, answer);

        return answer;
    }

    private void collectContainers(List<Container> userList, ArrayList<Container> answer) {
        for (Container container : userList) {
            if (!answer.contains(container) && container.isActive()) {
                answer.add(container);
            }
        }
    }

    public List<ContainerItemDTO> findPendingContainers() {
        return containerRepository.findPendingContainers();
    }

    private void postPopulate() {
        testCaseService.cleanNewTests();
    }

    // Check that the TestExecution.TestCase is not creating a new TestCase if it's already exist.
    private void assureTestUniqueness(List<Executor> newExecutors) {
        log.warning("AssureTestUniqueness. This potentially could cause OutOfMemory");
        for (Executor executor : newExecutors) {
            for (Build build : executor.getBuilds()) {
                for (TestExecution testExecution : build.getTestCases()) {
                    if (testExecution.getTestCase().getId() == null || testExecution.getTestCase().getId() < 1) {
                        TestCase testCase = testCaseService.newOrFind(testExecution.getTestCase());
                        testExecution.setTestCase(testCase);
                    }
                }
            }
        }
    }

    public List<Container> findContainersPendingToProcess() {
        return containerRepository.findContainersPendingToProcess();
    }

    public Boolean isValid(Long id) {

        Container container = find(id);
        CIConnector ciconnector = CIConnector.getConnector(container).connect();
        if (ciconnector == null) {
            String msg = String.format("Could not connect to Connector(id=%d)", container.getConnector().getId());
            log.severe(getSystemError(msg));
            log.log(Level.INFO, msg);
            return false;
        }

        try {

            if (container.isPushMode()) {
                String msg = String.format("Container (name=%s) is in %s mode. You doest'n have access.", container.getName(), container.getPopulateMode().name());
                log.severe(getSystemError(msg));
                log.log(Level.INFO, msg);
                return true;
            }

            Container containerToUpdate = ciconnector.containerExists(container);
            if (containerToUpdate != null) {
                container.setHiddenData(containerToUpdate.getRawHiddenData());
                return update(container) != null;
            }

            return false;
        } catch (ContainerServiceException e) {
            String msg = String.format("Could not find Container(name=%s)", container.getName());
            log.severe(getSystemError(msg));
            log.log(Level.INFO, msg);
            return false;
        }
    }

    public List<KeyValuePair> getContainersNames(Long id) {
        Product product = productService.find(id);
        List<Object[]> list = containerRepository.findAllNames(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<Container> getContainersByUser(User user) {
        return containerRepository.findAllByUser(user);
    }

    public List<Long> findContainersToPopulate() {
        return containerRepository.findContainersToPopulate(true, PopulateMode.PULL);
    }

    public Container findFirst() {
        return containerRepository.findFirstByEnabled(true);
    }
}
