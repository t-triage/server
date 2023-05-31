package com.clarolab.logtriage.mapper;

import com.clarolab.logtriage.dto.ErrorCaseDTO;
import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.logtriage.service.ErrorCaseService;
import com.clarolab.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.clarolab.mapper.MapperHelper.getIDList;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Service
public class ErrorCaseMapper implements Mapper<ErrorCase, ErrorCaseDTO> {

    @Autowired
    private ErrorCaseService errorCaseService;


    @Override
    public ErrorCaseDTO convertToDTO(ErrorCase errorCase) {
        ErrorCaseDTO dto = new ErrorCaseDTO();

        setEntryFields(errorCase, dto);

        dto.setLevel(errorCase.getLevel());
        dto.setMessage(errorCase.getMessage());
        dto.setPath(errorCase.getPath());
        dto.setStackTrace(errorCase.getStackTrace());
        dto.setThread(errorCase.getThread());
        dto.setEvents(getIDList(errorCase.getEvents()));

        return dto;
    }

    @Override
    public ErrorCase convertToEntity(ErrorCaseDTO dto) {
        ErrorCase error;

        if (dto.getId() == null || dto.getId() < 1) {
            error = ErrorCase.builder()
                    .path(dto.getPath())
                    .stackTrace(dto.getStackTrace())
                    .thread(dto.getThread())
                    .level(dto.getLevel())
                    .message(dto.getMessage())
                    .build();
        } else {
            error = errorCaseService.find(dto.getId());
            error.setLevel(dto.getLevel());
            error.setMessage(dto.getMessage());
            error.setPath(dto.getPath());
            error.setStackTrace(dto.getStackTrace());
            error.setThread(dto.getThread());
        }

        return error;
    }
}
