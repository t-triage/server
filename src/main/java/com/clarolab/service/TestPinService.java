/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.TestPin;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestPinRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class TestPinService extends BaseService<TestPin> {

    @Autowired
    private TestPinRepository testPinRepository;

    @Override
    public BaseRepository<TestPin> getRepository() {
        return testPinRepository;
    }
}
