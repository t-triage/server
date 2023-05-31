package com.clarolab.logtriage.controller.impl;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.logtriage.controller.EventExecutionController;
import com.clarolab.logtriage.dto.EventExecutionDTO;
import com.clarolab.logtriage.serviceDTO.EventExecutionServiceDTO;
import com.clarolab.model.Product;
import com.clarolab.service.ProductService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class EventExecutionControllerImpl extends BaseControllerImpl<EventExecutionDTO> implements EventExecutionController {

    @Autowired
    private ProductService productService;

    @Autowired
    private EventExecutionServiceDTO serviceDTO;

    @Override
    protected TTriageService<EventExecutionDTO> getService() {
        return serviceDTO;
    }

    @Override
    public ResponseEntity<List<EventExecutionDTO>> getEventsFromErrorCase(Long errorCaseId) {
        return ResponseEntity.ok(serviceDTO.getEventsFromErrorCase(errorCaseId));
    }

    @Override
    public ResponseEntity<Boolean> importEventsLog(String file, Long productId) {
        Product product = productService.find(productId);
        if (product != null) {
            serviceDTO.importEventsLog(file, product);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
