package com.clarolab.logtriage.serviceDTO;

import com.clarolab.logtriage.dto.SearchExecutorDTO;
import com.clarolab.logtriage.mapper.SearchExecutorMapper;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.service.SearchExecutorService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchExecutorServiceDTO implements BaseServiceDTO<SearchExecutor, SearchExecutorDTO, SearchExecutorMapper> {

    @Autowired
    private SearchExecutorService service;

    @Autowired
    private SearchExecutorMapper mapper;

    @Override
    public TTriageService<SearchExecutor> getService() {
        return service;
    }

    @Override
    public Mapper<SearchExecutor, SearchExecutorDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<SearchExecutor, SearchExecutorDTO, SearchExecutorMapper> getServiceDTO() {
        return this;
    }
}
