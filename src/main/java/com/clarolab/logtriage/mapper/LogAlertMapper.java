package com.clarolab.logtriage.mapper;

import com.clarolab.logtriage.dto.LogAlertDTO;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.service.LogAlertService;
import com.clarolab.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getIDList;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class LogAlertMapper implements Mapper<LogAlert, LogAlertDTO> {

    @Autowired
    private SearchExecutorMapper searchExecutorMapper;

    @Autowired
    private LogAlertService service;

    @Override
    public LogAlertDTO convertToDTO(LogAlert alert) {
        LogAlertDTO logAlertDTO = new LogAlertDTO();

        setEntryFields(alert, logAlertDTO);

        logAlertDTO.setAppName(alert.getAppName());
        logAlertDTO.setDate(alert.getDate());
        logAlertDTO.setHost(alert.getHost());
        logAlertDTO.setOwner(alert.getOwner());
        logAlertDTO.setSid(alert.getSid());
        logAlertDTO.setLastCheck(alert.getLastCheck());
        logAlertDTO.setUrl(alert.getUrl());
        logAlertDTO.setSearchExecutor(searchExecutorMapper.convertToDTO(alert.getSearchExecutor()));
        logAlertDTO.setEvents(getIDList(alert.getEvents()));

        return logAlertDTO;
    }

    @Override
    public LogAlert convertToEntity(LogAlertDTO dto) {
        LogAlert alert;
        if (dto.getId() == null || dto.getId() < 1) {
            alert = LogAlert.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .appName(dto.getAppName())
                    .date(dto.getDate())
                    .host(dto.getHost())
                    .owner(dto.getOwner())
                    .sid(dto.getSid())
                    .lastCheck(dto.getLastCheck())
                    .url(dto.getUrl())
                    .build();
        } else {
            alert = service.find(dto.getId());
            alert.setEnabled(dto.getEnabled());
            alert.setSid(dto.getSid());
        }

        return alert;
    }
}
