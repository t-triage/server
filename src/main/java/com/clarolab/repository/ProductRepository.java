/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product> {

    @Query("SELECT name, id FROM Product WHERE enabled = true ORDER BY name asc ")
    List<Object[]> findAllNames();

    Product findFirstByNameIgnoreCaseAndEnabledOrderByIdDesc(String name, boolean enabled);

    List<Product> findAllByPackageNamesIgnoreCaseContains(String packageName);

    Product findProductById(Long id);

    @Query("SELECT count(c) FROM CVSLog as c WHERE c.product = ?1")
    int countCvsLogsByProductId(Product product, boolean enabled);

    @Query("SELECT count(c) FROM CVSLog as c WHERE c.product = ?1 AND c.enabled = ?2 AND c.timestamp > ?3 AND c.timestamp < ?4")
    long countCvsLogsByProductIdAndTimestamp(Product product, boolean enabled, Long prev, Long now);

    Product findFirstById(Long productId);
}
