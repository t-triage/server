package com.clarolab.serviceDTO;

import com.clarolab.dto.LicenseDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.LicenseMapper;
import com.clarolab.service.LicenseService;
import com.clarolab.service.TTriageService;
import com.clarolab.startup.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LicenseServiceDTO implements BaseServiceDTO<License, LicenseDTO, LicenseMapper> {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private LicenseMapper mapper;

    @Override
    public TTriageService<License> getService() {
        return licenseService;
    }

    @Override
    public Mapper<License, LicenseDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<License, LicenseDTO, LicenseMapper> getServiceDTO() {
        return this;
    }

    public LicenseDTO getLicense() {
        return mapper.convertToDTO(licenseService.getLicense());
    }


}
