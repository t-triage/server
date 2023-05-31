/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.ContainerItemDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ContainerMapper;
import com.clarolab.model.Container;
import com.clarolab.model.User;
import com.clarolab.service.ContainerService;
import com.clarolab.service.TTriageService;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContainerServiceDTO implements BaseServiceDTO<Container, ContainerDTO, ContainerMapper> {

    @Autowired
    private ContainerService service;

    @Autowired
    private ContainerMapper mapper;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Override
    public TTriageService<Container> getService() {
        return service;
    }

    @Override
    public Mapper<Container, ContainerDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Container, ContainerDTO, ContainerMapper> getServiceDTO() {
        return this;
    }

    public Boolean populate( Long id){
        return service.populate(id);
    }

    public Boolean populate(String name){
        return service.populate(name);
    }

    public List<ContainerDTO> suggested(){

        User user = authContextHelper.getCurrentUser();

        List<Container> containers = service.suggested(user);
        List<ContainerDTO> answer = new ArrayList<>(containers.size());

        List<ContainerItemDTO> items = service.findPendingContainers();

        for (ContainerItemDTO containerDTO: items) {
            if (containerDTO.getUser() != null) {
                containerDTO.setUserDTO(userServiceDTO.convertToDTO(containerDTO.getUser()));
            }
        }
        final int itemSize = items.size();

        ContainerDTO dto = null;
        boolean found = false;
        int index = 0;
        ContainerItemDTO item;
        // Merges teh containers and items in order to populate dto.setPendingBuildTriages
        for (Container container : containers) {
            dto = convertToDTO(container);
            found = false;
            index = 0;
            while (!found && index < itemSize) {
                item = items.get(index);
                if (container.getId().equals(item.getConainerId())) {
                    found = true;
                    dto.setPendingBuildTriages((int) item.getPendingBuilds());
                    dto.setLoggedOwner(item.getUserDTO().getId().equals(user.getId()));
                }
                index ++;
            }
            answer.add(dto);
        }

        return answer;
    }

    public Boolean isValid(Long id) {
        return service.isValid(id);
    }

    public List<KeyValuePair> getContainersNames(Long id) {
        return service.getContainersNames(id);
    }
}
