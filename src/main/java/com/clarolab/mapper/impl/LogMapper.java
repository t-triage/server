package com.clarolab.mapper.impl;

import com.clarolab.dto.LogDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.CVSLog;
import com.clarolab.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class LogMapper implements Mapper<CVSLog, LogDTO> {

    @Autowired
    private LogService logService;

    @Override
    public LogDTO convertToDTO(CVSLog log) {
        LogDTO logDTO = new LogDTO();

        setEntryFields(log, logDTO);

        logDTO.setAuthor(log.getAuthorText());
        logDTO.setApprover(log.getApproverText());
        logDTO.setCommitHash(log.getCommitHash());
        logDTO.setDate(log.getCommitDate());
        logDTO.setTest(log.getCodeModified());
        logDTO.setAuthorId(log.getAuthor() == null ? 0 : log.getAuthor().getId());
        logDTO.setTestId(log.getTest() == null ? 0 : log.getTest().getId());

        return logDTO;
    }

    @Override
    public CVSLog convertToEntity(LogDTO dto) {
        CVSLog log;
        if (dto.getId() == null || dto.getId() < 1) {
            log = CVSLog.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .authorText(dto.getAuthor())
                    .approverText(dto.getApprover())
                    .commitHash(dto.getCommitHash())
                    .commitDate(dto.getDate())
                    .codeModified(dto.getTest())
                    .build();
        } else {
            log = logService.find(dto.getId());
        }
        return log;
    }
}
