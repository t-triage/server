/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ProductComponentDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.service.ManualTestRequirementService;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.model.manual.service.ProductComponentService;
import com.clarolab.service.ProductService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ManualTestStepServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ProductComponentMapper implements Mapper<ProductComponent, ProductComponentDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ManualTestRequirementService manualTestRequirementService;

    @Autowired
    private ManualTestStepServiceDTO manualTestStepServiceDTO;

    @Override
    public ProductComponentDTO convertToDTO(ProductComponent productComponent) {
        /* El productComponent en esta capa NO deberia ser null. Si llega null es porque hay algo mal
       /* if (productComponent == null) {
            return null;
        }*/
        ProductComponentDTO productComponentDTO = new ProductComponentDTO();

        setEntryFields(productComponent, productComponentDTO);

        productComponentDTO.setName(productComponent.getName());
        productComponentDTO.setDescription(productComponent.getDescription());
        productComponentDTO.setProductId(productComponent.getProduct() == null ? 0L : productComponent.getProduct().getId());

        return productComponentDTO;
    }

    @Override
    public ProductComponent convertToEntity(ProductComponentDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductComponent productComponent;
        if (dto.getId() == null || dto.getId() < 1) {
            productComponent = ProductComponent.builder()
                    .id(null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .product(getNullableByID(dto.getProductId(), id -> productService.find(id)))
                    .build();

        } else {
            productComponent = productComponentService.find(dto.getId());
//            productComponent.setId(); Don't allow to update this.
            productComponent.setEnabled(dto.getEnabled());
//            productComponent.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            productComponent.setUpdated(dto.getUpdated()); Don't allow to update this.
            productComponent.setName(dto.getName());
            productComponent.setDescription(dto.getDescription());
            productComponent.setProduct(getNullableByID(dto.getProductId(), id -> productService.find(id)));
        }

        return productComponent;
    }

}
