/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ProductDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ProductMapper;
import com.clarolab.model.Product;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProductMapperTest extends AbstractMapperTest<Product, ProductDTO> {

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private UseCaseDataProvider useCaseDataProvider;

    @Test
    public void testEntityToDTOConversion() {
        Product product = getEntity();

        product.setNote(useCaseDataProvider.getNote());

        ProductDTO productDTO = mapper.convertToDTO(product);
        this.assertConversion(product, productDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ProductDTO productDTO = getDTO();
        Product product = mapper.convertToEntity(productDTO);
        this.assertConversion(product, productDTO);
    }

    @Override
    public void assertConversion(Product product, ProductDTO productDTO) {
        super.assertConversion(product, productDTO);

        Assert.assertEquals(product.getName(), productDTO.getName());
        Assert.assertEquals(product.getDescription(), productDTO.getDescription());
    }

    public ProductMapperTest() {
        super(Product.class, ProductDTO.class);
    }
}
