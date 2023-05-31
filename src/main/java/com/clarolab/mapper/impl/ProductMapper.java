/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ProductDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.ImageModel;
import com.clarolab.model.Product;
import com.clarolab.model.helper.ImageHelper;
import com.clarolab.service.ContainerService;
import com.clarolab.service.DeadlineService;
import com.clarolab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.clarolab.mapper.MapperHelper.getIDList;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ProductMapper implements Mapper<Product, ProductDTO> {

    @Autowired
    private ProductService productService;
    @Autowired
    private DeadlineService deadlineService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private NoteMapper noteMapper;

    @Override
    @Transactional
    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        setEntryFields(product, productDTO);

        productDTO.setName(product.getName());
        productDTO.setContainers(getIDList(product.getContainers()));
        productDTO.setDeadlines(getIDList(product.getDeadlines()));
        productDTO.setDescription(product.getDescription());
        productDTO.setLogo(product.getLogo() == null ? null : product.getLogo().getData());
        productDTO.setRepositories(getIDList(product.getRepositories()));
        productDTO.setPackageNames(product.getPackageNames());
        productDTO.setLogPattern(product.getLogPattern());
        productDTO.setHasMultipleEnvironment(product.isHasMultipleEnvironment());

        productDTO.setNote(product.getNote() == null ? null : noteMapper.convertToDTO(product.getNote()));

        return productDTO;
    }

    @Override
    @Transactional
    public Product convertToEntity(ProductDTO dto) {
        Product product;
        if (dto.getId() == null || dto.getId() < 1) {
            product = Product.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .hasMultipleEnvironment(dto.isHasMultipleEnvironment())
                    .note(noteMapper.convertToEntity(dto.getNote()))
                    .logo(dto.getLogo() == null ? null : ImageModel
                            .builder()
                            .enabled(dto.getEnabled())
                            .timestamp(dto.getTimestamp())
                            .updated(dto.getUpdated())
                            .description("Image from: " + dto.getName())
                            .name("Image from: " + dto.getName())
                            .data(ImageHelper.compressBase64Image(dto.getLogo()))//260263
                            .build())//TODO fede back to here an do it in a better way
                    .packageNames(dto.getPackageNames())
                    .logPattern(dto.getLogPattern())
                    .build();
        } else {
            product = productService.find(dto.getId());
//            product.setId(); Don't allow to update this.
            product.setEnabled(dto.getEnabled());
//            product.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            product.setUpdated(dto.getUpdated()); Don't allow to update this.
            product.setName(dto.getName());
            product.setNote(noteMapper.convertToEntity(dto.getNote()));
            product.setDescription(dto.getDescription());
            product.setHasMultipleEnvironment(dto.isHasMultipleEnvironment());
            product.setLogo(ImageModel.builder().updated(dto.getUpdated()).data(dto.getLogo()).build());
            product.setPackageNames(dto.getPackageNames());
            product.setLogPattern(dto.getLogPattern());

        }
        return product;
    }
}
