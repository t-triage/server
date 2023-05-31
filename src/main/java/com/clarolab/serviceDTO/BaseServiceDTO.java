/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.BaseDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Entry;
import com.clarolab.service.TTriageService;
import com.clarolab.service.exception.NotFoundServiceException;
import com.clarolab.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.clarolab.util.SearchSpecificationUtil.getSearchSpec;

public interface BaseServiceDTO<Entity extends Entry, DTO extends BaseDTO, M extends Mapper> extends TTriageService<DTO> {

    TTriageService<Entity> getService();

    Mapper<Entity, DTO> getMapper();

    BaseServiceDTO<Entity, DTO, M> getServiceDTO();

    @Override
    default void delete(Long id) throws ServiceException {
        this.disable(id);
    }

    @Override
    default void disable(Long id) throws ServiceException {
        this.getService().disable(id);
    }

    @Override
    default DTO update(DTO dto) throws ServiceException {
        return getMapper().convertToDTO(this.updateToEntity(dto));
    }

    @Override
    default DTO save(DTO dto) throws ServiceException {
        return getMapper().convertToDTO(saveToEntity(dto));
    }

    default Entity updateToEntity(DTO dto) throws ServiceException {
        Entity entity = getMapper().convertToEntity(dto);
        return this.getService().update(entity);
    }

    default Entity saveToEntity(DTO dto) throws ServiceException {
        Entity entity = getMapper().convertToEntity(dto);
        return this.getService().save(entity);
    }

    @Override
    default Page<DTO> findAll(@Nullable Specification spec, @NotNull Pageable pageable) {
        Page<Entity> page = this.getService().findAll(spec, pageable);
        List<DTO> list = convertToDTO(page.getContent());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    default Page<DTO> findAll(@Nullable String[] criteria, @NotNull Pageable pageable) {
        Page<Entity> page = this.getService().findAll(criteria, pageable);
        List<DTO> list = convertToDTO(page.getContent());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    default List<DTO> findAll(@Nullable String[] criteria, @Nullable Sort sort) {
        return findAll(getSearchSpec(criteria), sort);
    }

    @Override
    default List<DTO> findAll() {
        return findAll(getSearchSpec(null), Sort.unsorted());
    }

    @Override
    default List<DTO> findAll(@Nullable Specification spec, @Nullable Sort sort) {
        return convertToDTO(getService().findAll(spec, sort));
    }

    @Override
    default DTO find(Long id) throws ServiceException {
        DTO dto;
        try {
            Entity entity = this.getService().find(id);
            if (entity == null)
                throw new NotFoundServiceException("Entity not found");

            dto = getMapper().convertToDTO(entity);
        } catch (Exception e) {
            throw new NotFoundServiceException(e);
        }
        return dto;
    }

    default Entity findEntity(Long id) throws ServiceException {
        return this.getService().find(id);
    }

    default Entity findEntity(DTO dto) throws ServiceException {
        return findEntity(dto.getId());
    }

    @Override
    default long count(@Nullable Specification spec) {
        return this.getService().count(spec);
    }

    @Override
    default long count() {
        return this.getService().count();
    }

    @Override
    default long countEnabled() {
        return this.getService().countEnabled();
    }

    @Override
    default long countDisabled() {
        return this.getService().countDisabled();
    }

    default DTO convertToDTO(Entity entity){
        return getMapper().convertToDTO(entity);
    }

    default Entity convertToEntity(DTO dto){
        return getMapper().convertToEntity(dto);
    }

    default List<Entity> convertToEntity(List<DTO> dtoList) {
        return dtoList
                .stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    default List<DTO> convertToDTO(List<Entity> entities) {
        List<DTO> answer = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            answer.add(this.convertToDTO(entity));
        }
        return answer;
    }

}
