/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.slack;

import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;

public interface SlackSpecRepository extends BaseRepository<SlackSpec> {

    SlackSpec findByExecutor(Executor executor);
    
    SlackSpec findByProductAndContainerAndExecutor(Product product, Container container, Executor executor);

}
