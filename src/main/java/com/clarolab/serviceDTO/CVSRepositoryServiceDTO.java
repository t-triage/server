package com.clarolab.serviceDTO;

import com.clarolab.dto.CVSRepositoryDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.CVSRepositoryMapper;
import com.clarolab.model.CVSRepository;
import com.clarolab.service.CVSRepositoryService;
import com.clarolab.service.TTriageService;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CVSRepositoryServiceDTO implements BaseServiceDTO<CVSRepository, CVSRepositoryDTO, CVSRepositoryMapper>{

    @Autowired
    private CVSRepositoryService service;

    @Autowired
    private CVSRepositoryMapper mapper;

    @Override
    public TTriageService<CVSRepository> getService() {
        return service;
    }

    @Override
    public Mapper<CVSRepository, CVSRepositoryDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<CVSRepository, CVSRepositoryDTO, CVSRepositoryMapper> getServiceDTO() {
        return this;
    }

    public List<KeyValuePair> getCvsRepositoriesNames() {
        return service.getCvsRepositoriesNames();
    }
}
