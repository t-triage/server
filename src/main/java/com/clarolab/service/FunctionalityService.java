/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.model.manual.Functionality;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.FunctionalityRepository;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class FunctionalityService extends BaseService<Functionality> {

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Override
    public BaseRepository<Functionality> getRepository() {
        return functionalityRepository;
    }

    @Override
    public Functionality save(Functionality entry){
        return super.save(entry);
    }

    public Functionality findFunctionalityByName(String name) {
        if (name == null) {
            return null;
        }
        return functionalityRepository.findFirstByNameIgnoreCaseAndEnabledOrderByIdDesc(name.trim(), true);
    }
    
    public List<Functionality> search(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        return functionalityRepository.searchFunctionality(name, true);
    }
    
    public Functionality findByExternalId(String externalId){
        if (externalId == null || externalId.length() < MIN_SEARCH_LENGHT_) {
            return null;
        }
        return functionalityRepository.findFirstByExternalIdIgnoreCaseAndEnabledOrderByIdDesc(externalId, true);
    }
    
}