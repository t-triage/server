/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ProductRepository;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class ProductService extends BaseService<Product> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public BaseRepository<Product> getRepository() {
        return productRepository;
    }

    public List<KeyValuePair> getProductNames() {
        List<Object[]> list = productRepository.findAllNames();
        return StringUtils.getKeyValuePairList(list);
    }

    public Product findProductById(Long id){
        return productRepository.findProductById(id);
    }

    public Product findProductByName(String name) {
        if (name == null) {
            return null;
        }
        return productRepository.findFirstByNameIgnoreCaseAndEnabledOrderByIdDesc(name.trim(), true);
    }

    public List<Product> findProductsByPackageNames(String packageName) {
        if (packageName == null)
            return null;
        return productRepository.findAllByPackageNamesIgnoreCaseContains(packageName);
    }

    public int countCvsLogsByProductId(Product product, boolean value) {
        return productRepository.countCvsLogsByProductId(product, value);
    }

    public long countCvsLogsByProductIdAndTimestamp(Product product, boolean value, Long prev, Long now) {
        return productRepository.countCvsLogsByProductIdAndTimestamp(product, value, prev, now);
    }    
}
