/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Productivity;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ProductivityRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class ProductivityService extends BaseService<Productivity> {

    @Autowired
    private ProductivityRepository productivityRepository;

    @Override
    public BaseRepository<Productivity> getRepository() {
        return productivityRepository;
    }


}
