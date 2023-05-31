/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.model.ImageModel;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ImageModelRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class ImageModelService extends BaseService<ImageModel> {

    @Autowired
    private ImageModelRepository imageModelRepository;

    @Override
    public BaseRepository<ImageModel> getRepository() {
        return imageModelRepository;
    }
}
