/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;


import com.clarolab.model.Product;
import com.clarolab.model.ProductGoal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductGoalRepository extends BaseRepository<ProductGoal> {

    List<Product> findAllByEnabled(boolean enabled);


}
