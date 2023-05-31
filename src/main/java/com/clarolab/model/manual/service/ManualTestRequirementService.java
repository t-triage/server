/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.repository.ManualTestRequirementRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class ManualTestRequirementService extends BaseService<ManualTestRequirement> {

    @Autowired
    private ManualTestRequirementRepository manualTestRequirementRepository;

    @Override
    public BaseRepository<ManualTestRequirement> getRepository() {
        return manualTestRequirementRepository;
    }
}
