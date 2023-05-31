package com.clarolab.logtriage.mapper;

import com.clarolab.logtriage.dto.EventExecutionDTO;
import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.service.EventExecutionService;
import com.clarolab.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class EventExecutionMapper implements Mapper<EventExecution, EventExecutionDTO> {

    @Autowired
    private LogAlertMapper logAlertMapper;

    @Autowired
    private ErrorCaseMapper errorCaseMapper;

    @Autowired
    private EventExecutionService eventExecutionService;

    @Override
    public EventExecutionDTO convertToDTO(EventExecution eventExecution) {
        EventExecutionDTO dto = new EventExecutionDTO();

        setEntryFields(eventExecution, dto);

        dto.setContent(eventExecution.getContent());
        dto.setDate(eventExecution.getDate());
        dto.setHost(eventExecution.getHost());
        dto.setIndexedTime(eventExecution.getIndexedTime());
        dto.setSource(eventExecution.getSource());
        dto.setSourceType(eventExecution.getSourceType());
        dto.setAlert(logAlertMapper.convertToDTO(eventExecution.getAlert()));
        dto.setErrorCase(errorCaseMapper.convertToDTO(eventExecution.getError()));

        return dto;
    }

    @Override
    public EventExecution convertToEntity(EventExecutionDTO dto) {
        EventExecution event;

        if (dto.getId() == null || dto.getId() < 1) {
            event = EventExecution.builder()
                    .content(dto.getContent())
                    .date(dto.getDate())
                    .host(dto.getHost())
                    .source(dto.getSource())
                    .sourceType(dto.getSourceType())
                    .build();
        } else {
            event = eventExecutionService.find(dto.getId());
            event.setContent(dto.getContent());
            event.setDate(dto.getDate());
            event.setHost(dto.getHost());
            event.setIndexedTime(dto.getIndexedTime());
            event.setSource(dto.getSource());
            event.setSourceType(dto.getSourceType());
        }

        return event;
    }
}
