/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.SlackSpecDTO;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.SlackSpecMapper;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.service.ContainerService;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.ProductService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlackSpecServiceDTO implements BaseServiceDTO<SlackSpec, SlackSpecDTO, SlackSpecMapper> {

    @Autowired
    private SlackSpecService service;

    @Autowired
    private SlackSpecMapper mapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private ExecutorService executorService;

    @Override
    public TTriageService<SlackSpec> getService() {
        return service;
    }

    @Override
    public Mapper<SlackSpec, SlackSpecDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<SlackSpec, SlackSpecDTO, SlackSpecMapper> getServiceDTO() {
        return this;
    }

    public SlackSpecDTO findContainer(Long id) {
        Container container = containerService.find(id);
        if (container == null) {
            return null;
        }
        SlackSpec entity = service.find(container);

        if (entity == null) {
            return null;
        } else {
            return convertToDTO(entity);
        }
    }

    public SlackSpecDTO findExecutor(Long id) {
        Executor executor = executorService.find(id);
        if (executor == null) {
            return null;
        }
        SlackSpec entity = service.find(executor);

        if (entity == null) {
            return null;
        } else {
            return convertToDTO(entity);
        }
    }

    public String sendTestMessage(Long containerId) {
        Container container = containerService.find(containerId);
        if (container == null) {
            return "Couldn't find container";
        }
        return slackSpecService.sendTestMessage(container);
    }
    @Override
    public SlackSpecDTO save(SlackSpecDTO dto){
        if (dto.getExecutorId() == null){
            //crear slack spec nuevo
            SlackSpec spec = mapper.convertToEntity(dto);
            slackSpecService.save(spec);
            return dto;
        }

        Executor ex = executorService.find(dto.getExecutorId());
        SlackSpec executorSPec = slackSpecService.find(ex);

        if (executorSPec != null ){

            executorSPec.setChannel(dto.getChannel());
            executorSPec.setDailyChannel(dto.getDailyChannel());
            executorSPec.setToken(dto.getToken());

            slackSpecService.save(executorSPec);
            return dto;
        }

        SlackSpec spec = mapper.convertToEntity(dto);
        slackSpecService.save(spec);
        return dto;
    }

    @Override
    public SlackSpecDTO update(SlackSpecDTO dto){
        SlackSpec spec = service.find(mapper.convertToEntity(dto).getId());
        slackSpecService.save(spec);
        return dto;
    }
}
