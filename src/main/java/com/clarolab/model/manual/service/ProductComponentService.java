/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.dto.db.ComponentsDTO;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.repository.ProductComponentRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class ProductComponentService extends BaseService<ProductComponent> {

    @Autowired
    private ProductComponentRepository productComponentRepository;

    @Override
    public BaseRepository<ProductComponent> getRepository() {
        return productComponentRepository;
    }

    public List<ProductComponent> findAll(){
        return productComponentRepository.findAllByEnabled(true);
    }

    public List<ProductComponent> search(String name, boolean defaultComponents1) {
        if (defaultComponents1){
            return productComponentRepository.search1();
        }
        if (name == null ||name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        return productComponentRepository.search(name);

    }

    public List<ProductComponent> searchStrict(String name) {
        return productComponentRepository.findAllByNameIgnoreCaseLikeAndEnabled(name, true);

    }

    public List<ProductComponent> suggested(ProductComponent component) {

        List<ComponentsDTO> componentsDTOS = productComponentRepository.search(component);
        Set<ProductComponent> answer = new HashSet<>();

        for (ComponentsDTO componentsDTO: componentsDTOS) {
            answer.addAll(componentsDTO.getComponents());
        }
        answer.remove(component);
        List<ProductComponent> sortedAnswer = answer.stream().collect(Collectors.toCollection(ArrayList::new));
        sortedAnswer.sort(Comparator.comparing(ProductComponent::getName));
        return  sortedAnswer;
    }
    public List<ProductComponent> suggested(ProductComponent component1, ProductComponent component2) {

        List<ComponentsDTO> componentsDTOS = productComponentRepository.search(component1, component2);
        Set<ProductComponent> answer = new HashSet<>();

        for (ComponentsDTO componentsDTO: componentsDTOS) {
            answer.addAll(componentsDTO.getComponents());
        }
        answer.remove(component1);
        answer.remove(component2);
        List<ProductComponent> sortedAnswer = answer.stream().collect(Collectors.toCollection(ArrayList::new));
        sortedAnswer.sort(Comparator.comparing(ProductComponent::getName));
        return  sortedAnswer;
    }

}
