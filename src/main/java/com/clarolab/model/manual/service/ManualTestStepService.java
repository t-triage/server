/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.repository.ManualTestStepRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class ManualTestStepService extends BaseService<ManualTestStep> {

    @Autowired
    private ManualTestStepRepository manualTestStepRepository;

    @Override
    public BaseRepository<ManualTestStep> getRepository() {
        return manualTestStepRepository;
    }

    public ManualTestStep findStep(ManualTestCase manualTestCase, long externalId) throws PersistenceException {
            List<ManualTestStep> steps = manualTestStepRepository.findAllByTestCaseAndExternalId(manualTestCase, externalId);
            
            if (steps.isEmpty()) {
                return null;
            } else {
                if(steps.size() > 1) {
                    log.log(Level.WARNING, String.format("There are %d steps with the same id for test id: %d", externalId, manualTestCase.getId()));
                }
                return steps.get(0);
            }
    }

    public List<ManualTestStep> findAllByTest(ManualTestCase manualTestCase) {
        return manualTestStepRepository.findAllByTestCase(manualTestCase);
    }
}
