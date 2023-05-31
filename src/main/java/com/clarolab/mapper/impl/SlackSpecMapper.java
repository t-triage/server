/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.SlackSpecDTO;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.ContainerService;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.ProductService;
import com.clarolab.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class SlackSpecMapper implements Mapper<SlackSpec, SlackSpecDTO> {

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ExecutorService executorService;

    @Override
    public SlackSpecDTO convertToDTO(SlackSpec entity) {
        SlackSpecDTO dto = new SlackSpecDTO();

        setEntryFields(entity, dto);

        dto.setChannel(entity.getChannel());
        dto.setDailyChannel(entity.getDailyChannel());
        dto.setChannel(entity.getChannel());
        dto.setToken(entity.getToken());

        dto.setProductId(entity.getProduct().getId());
        dto.setContainerId(entity.getContainer() == null ? null : entity.getContainer().getId());
        dto.setExecutorId(entity.getExecutor() == null ? null : entity.getExecutor().getId());
        dto.setParentId(entity.getParent() == null ? null : entity.getParent().getId());
        dto.setSendUserNotification(entity.isSendUserNotification());
        dto.setSendDailyNotification(entity.isSendDailyNotification());

        return dto;

    }

    @Override
    public SlackSpec convertToEntity(SlackSpecDTO dto) {
        SlackSpec entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = SlackSpec.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .channel(dto.getChannel())
                    .dailyChannel(dto.getDailyChannel())
                    .token(dto.getToken())
                    .parent(slackSpecService.find(dto.getParentId()))
                    .product(productService.find(dto.getProductId()))
                    .executor(executorService.find(dto.getExecutorId()))
                    .container(containerService.find(dto.getContainerId()))
                    .sendUserNotification(dto.isSendUserNotification())
                    .build();
        } else {
            entity = slackSpecService.find(dto.getId());
//            connector.setId(dto.getId()); Don't allow to update this.
            entity.setEnabled(dto.getEnabled());
//            connector.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            connector.setUpdated(dto.getUpdated()); Don't allow to update this.
            entity.setChannel(dto.getChannel());
            entity.setDailyChannel(dto.getDailyChannel());
            entity.setToken(dto.getToken());
            entity.setParent(slackSpecService.find(dto.getParentId()));
            entity.setProduct(productService.find(dto.getProductId()));
            entity.setContainer(containerService.find(dto.getContainerId()));
            entity.setExecutor(executorService.find(dto.getExecutorId()));
            entity.setSendUserNotification(dto.isSendUserNotification());
            entity.setSendDailyNotification(dto.isSendDailyNotification());
        }

        if (entity.getExecutor() != null && entity.getParent() == null) {
            SlackSpec containerParent = slackSpecService.find(entity.getContainer());
            if (containerParent == null) {
                SlackSpec productParent = slackSpecService.find(entity.getProduct());
                containerParent = SlackSpec.builder()
                        .id(null)
                        .enabled(dto.getEnabled())
                        .timestamp(dto.getTimestamp())
                        .updated(dto.getUpdated())
                        .channel(dto.getChannel())
                        .dailyChannel(dto.getDailyChannel())
                        .token(dto.getToken())
                        .parent(productParent)
                        .product(productService.find(dto.getProductId()))
                        .container(containerService.find(dto.getContainerId()))
                        .sendUserNotification(dto.isSendUserNotification())
                        .build();
                containerParent = slackSpecService.save(containerParent);
            }

            entity.setParent(containerParent);
        }

        if (entity.getExecutor() != null && entity.getParent() != null) {
            // cleans the token if they are the same
            if (!StringUtils.isEmpty(entity.getToken()) && entity.getToken().equalsIgnoreCase(entity.getParent().getToken())) {
                entity.setToken(null);
            }
        }

        return entity;
    }
}
