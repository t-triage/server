/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper;

import com.clarolab.dto.BaseDTO;
import com.clarolab.model.Entry;

public interface Mapper<Entity extends Entry, DTO extends BaseDTO> {

    DTO convertToDTO(Entity entity);

    Entity convertToEntity(DTO dto);

}
