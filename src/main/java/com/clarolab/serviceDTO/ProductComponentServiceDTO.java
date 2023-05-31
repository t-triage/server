/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ProductComponentDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ProductComponentMapper;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.service.ProductComponentService;
import com.clarolab.service.TTriageService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductComponentServiceDTO implements BaseServiceDTO<ProductComponent, ProductComponentDTO, ProductComponentMapper> {

    @Autowired
    private ProductComponentService service;

    @Autowired
    private ProductComponentMapper mapper;

    @Override
    public TTriageService<ProductComponent> getService() {
        return service;
    }

    @Override
    public Mapper<ProductComponent, ProductComponentDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ProductComponent, ProductComponentDTO, ProductComponentMapper> getServiceDTO() {
        return this;
    }

    public List<ProductComponentDTO> search(String name, boolean defaultComponents1) {
        return convertToDTO(service.search(name, defaultComponents1));
    }

    public List<ProductComponentDTO> suggested(Long component1, Long component2) {

        if ( component1 != null && component2 == null ) {
            return convertToDTO(service.suggested(this.convertToEntity(this.find(component1))));
        }
        if (component1 != null && component2 != null)
            return convertToDTO(service.suggested(this.convertToEntity(this.find(component1)), this.convertToEntity(this.find(component2))));
        return Lists.newArrayList();
    }

}
