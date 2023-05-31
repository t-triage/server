/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ProductDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ProductMapper;
import com.clarolab.model.Product;
import com.clarolab.service.ProductService;
import com.clarolab.service.TTriageService;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductServiceDTO implements BaseServiceDTO<Product, ProductDTO, ProductMapper> {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductMapper mapper;

    @Override
    public TTriageService<Product> getService() {
        return service;
    }

    @Override
    public Mapper<Product, ProductDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Product, ProductDTO, ProductMapper> getServiceDTO() {
        return this;
    }

    public List<KeyValuePair> getProductNames() {
        return service.getProductNames();
    }
}
