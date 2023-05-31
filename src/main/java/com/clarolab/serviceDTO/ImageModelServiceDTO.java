/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ImageModelDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ImageModelMapper;
import com.clarolab.model.ImageModel;
import com.clarolab.service.ImageModelService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageModelServiceDTO implements BaseServiceDTO<ImageModel, ImageModelDTO, ImageModelMapper> {

    @Autowired
    private ImageModelService service;

    @Autowired
    private ImageModelMapper mapper;

    @Override
    public TTriageService<ImageModel> getService() {
        return service;
    }

    @Override
    public Mapper<ImageModel, ImageModelDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ImageModel, ImageModelDTO, ImageModelMapper> getServiceDTO() {
        return this;
    }
}
