/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.details.TestDetailDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TestDetailMapper;
import com.clarolab.model.detail.TestDetail;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDetailServiceDTO implements BaseServiceDTO<TestDetail, TestDetailDTO, TestDetailMapper> {

    @Autowired
    private TestDetailService service;

    @Autowired
    private TestDetailMapper mapper;

    @Override
    public TTriageService<TestDetail> getService() {
        return service;
    }

    @Override
    public Mapper<TestDetail, TestDetailDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TestDetail, TestDetailDTO, TestDetailMapper> getServiceDTO() {
        return this;
    }

    public TestDetailDTO getTestDetail(Long testTriageId) {
        return convertToDTO(service.calculateTestDetails(testTriageId));
    }
}
