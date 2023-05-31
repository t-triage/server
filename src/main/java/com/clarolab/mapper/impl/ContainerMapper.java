/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ContainerDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Container;
import com.clarolab.model.Entry;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.ReportType;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.TriageSpecServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.clarolab.mapper.MapperHelper.*;


@Component
public class ContainerMapper implements Mapper<Container, ContainerDTO> {

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private TriageSpecServiceDTO triageSpecServiceDTO;

    @Override
    public ContainerDTO convertToDTO(Container container) {
        ContainerDTO containerDTO = new ContainerDTO();
        setEntryFields(container, containerDTO);
        containerDTO.setConnector(container.getConnector() == null ? null : container.getConnector().getId());
        containerDTO.setDescription(container.getDescription());
        containerDTO.setExecutors(getIDList(container.getExecutors().stream().filter(Entry::isEnabled).collect(Collectors.toList())));
        containerDTO.setName(container.getName());
        containerDTO.setProductName(container.getProduct() == null ? "" : container.getProduct().getName());
        containerDTO.setProduct(container.getProduct() == null ? null : container.getProduct().getId());
        containerDTO.setUrl(container.getUrl());
        containerDTO.setHiddenData(container.getRawHiddenData());
        containerDTO.setPopulateMode(container.getPopulateMode().name());
        containerDTO.setReportType(container.getReportType() != null ? container.getReportType().name() : null);
        containerDTO.setType(container.getConnector() == null ? null : container.getConnector().getType().name());
        long productCount = productService.countEnabled();
        if (productCount > 1) {
            containerDTO.setProductName(container.getProductName());
        }

        TriageSpec spec = triageSpecService.geTriageFlowSpecByContainer(container);
        containerDTO.setTriageSpec(spec == null ? null : triageSpecServiceDTO.convertToDTO(spec));

        return containerDTO;
    }

    @Override
    public Container convertToEntity(ContainerDTO dto) {
        Container container;
        if (dto.getId() == null || dto.getId() < 1) {
            container = Container.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .populateMode(PopulateMode.valueOf(dto.getPopulateMode()))
                    .reportType(ReportType.valueOf(dto.getReportType()))
                    .connector(getNullableByID(dto.getConnector(), id -> connectorService.find(id)))
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .hiddenData(dto.getHiddenData())
                    .product(getNullableByID(dto.getProduct(), id -> productService.find(id)))
                    .url(dto.getUrl())
                    .build();
        } else {
            container = containerService.find(dto.getId());

//            container.setId(dto.getId());
//            container.setUpdated(dto.getUpdated());
//            container.setTimestamp(dto.getTimestamp());
            container.setEnabled(dto.getEnabled());
            container.setName(dto.getName());
            container.setPopulateMode(PopulateMode.valueOf(dto.getPopulateMode()));
            container.setReportType(dto.getReportType() == null ? null : ReportType.valueOf(dto.getReportType()));
            container.setDescription(dto.getDescription());
            container.setHiddenData(dto.getHiddenData());
            container.setConnector(getNullableByID(dto.getConnector(), id -> connectorService.find(id)));
            container.setProduct(getNullableByID(dto.getProduct(), id -> productService.find(id)));
            container.setUrl(dto.getUrl());
        }

        triageSpecServiceDTO.convertToEntity(dto.getTriageSpec());

        return container;
    }
}
