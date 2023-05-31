package com.clarolab.logtriage.serviceDTO;

import com.clarolab.logtriage.dto.EventExecutionDTO;
import com.clarolab.logtriage.mapper.EventExecutionMapper;
import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.service.EventExecutionImportService;
import com.clarolab.logtriage.service.EventExecutionService;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Product;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BaseServiceDTO;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

@Log
@Service
public class EventExecutionServiceDTO implements BaseServiceDTO<EventExecution, EventExecutionDTO, EventExecutionMapper> {

    @Autowired
    private EventExecutionImportService importService;

    @Autowired
    private EventExecutionService service;

    @Autowired
    private EventExecutionMapper mapper;

    @Override
    public TTriageService<EventExecution> getService() {
        return service;
    }

    @Override
    public Mapper<EventExecution, EventExecutionDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<EventExecution, EventExecutionDTO, EventExecutionMapper> getServiceDTO() {
        return this;
    }

    public List<EventExecutionDTO> getEventsFromErrorCase(Long errorCase) {
        return convertToDTO(service.getEventsFromErrorCase(errorCase));
    }

    public void importEventsLog(String file, Product product) {
        String base64String = file.substring(file.indexOf(",") + 1);
        byte[] decodedString = Base64.getDecoder().decode(base64String.getBytes());
        InputStream myInputStream = new ByteArrayInputStream(decodedString);

        String content = null;

        try {
            content = IOUtils.toString(myInputStream, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Couldn't read the file content from the log import", ex);
        }

        if (content == null || content.isEmpty()) {
            log.log(Level.INFO, "The file content was null or empty");
        } else {
            log.log(Level.INFO, "Importing logs from file...");

            List<EventExecution> events = importService.read(content, product.getPackageNames(), product.getLogPattern());

            if (events.isEmpty()) {
                log.log(Level.INFO, "There was no error logs in the imported file");
            } else {
                service.saveAll(events);
                log.log(Level.INFO, String.format("Succesfully imported %d logs.", events.size()));
            }
        }
    }
}
