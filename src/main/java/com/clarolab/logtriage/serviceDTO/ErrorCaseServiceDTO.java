package com.clarolab.logtriage.serviceDTO;

import com.clarolab.logtriage.dto.ErrorCaseDTO;
import com.clarolab.logtriage.mapper.ErrorCaseMapper;
import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.logtriage.service.ErrorCaseService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorCaseServiceDTO implements BaseServiceDTO<ErrorCase, ErrorCaseDTO, ErrorCaseMapper> {

    @Autowired
    private ErrorCaseService service;

    @Autowired
    private ErrorCaseMapper mapper;

    @Override
    public TTriageService<ErrorCase> getService() {
        return service;
    }

    @Override
    public Mapper<ErrorCase, ErrorCaseDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ErrorCase, ErrorCaseDTO, ErrorCaseMapper> getServiceDTO() {
        return this;
    }
}
