/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.ProductGoal;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ProductGoalRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class ProductGoalService extends BaseService<ProductGoal> {

    @Autowired
    private ProductGoalRepository productGoalRepository;

    @Override
    public BaseRepository<ProductGoal> getRepository() {
        return productGoalRepository;
    }


}