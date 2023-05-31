package com.clarolab.logtriage.serviceDTO;

import com.clarolab.logtriage.dto.LogAlertDTO;
import com.clarolab.logtriage.mapper.LogAlertMapper;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.service.LogAlertService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogAlertServiceDTO implements BaseServiceDTO<LogAlert, LogAlertDTO, LogAlertMapper> {

    @Autowired
    private LogAlertService logAlertService;

    @Autowired
    private LogAlertMapper mapper;

    @Override
    public TTriageService<LogAlert> getService() {
        return this.logAlertService;
    }

    @Override
    public Mapper<LogAlert, LogAlertDTO> getMapper() {
        return this.mapper;
    }

    @Override
    public BaseServiceDTO<LogAlert, LogAlertDTO, LogAlertMapper> getServiceDTO() {
        return this;
    }
}
