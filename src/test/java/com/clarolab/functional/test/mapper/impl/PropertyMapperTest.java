/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.PropertyDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.PropertyMapper;
import com.clarolab.model.Property;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PropertyMapperTest extends AbstractMapperTest<Property, PropertyDTO> {

    @Autowired
    private PropertyMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        Property entity = getEntity();

        PropertyDTO dto = mapper.convertToDTO(entity);
        this.assertConversion(entity, dto);
    }

    @Test
    public void testDTOToEntityConversion() {
        PropertyDTO dto = getDTO();
        Property product = mapper.convertToEntity(dto);
        this.assertConversion(product, dto);
    }

    @Override
    public void assertConversion(Property entity, PropertyDTO dto) {
        super.assertConversion(entity, dto);

        Assert.assertEquals(entity.getName(), dto.getName());
        Assert.assertEquals(entity.getDescription(), dto.getDescription());
        Assert.assertEquals(entity.getValue(), dto.getValue());
    }

    public PropertyMapperTest() {
        super(Property.class, PropertyDTO.class);
    }
}
