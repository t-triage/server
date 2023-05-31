/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Deadline;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.DeadlineRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class DeadlineService extends BaseService<Deadline> {

    @Autowired
    private DeadlineRepository deadlineRepository;

    @Override
    public BaseRepository<Deadline> getRepository() {
        return deadlineRepository;
    }
}
