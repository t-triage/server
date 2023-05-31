package com.clarolab.controller.impl;

import com.clarolab.controller.CVSRepositoryController;
import com.clarolab.dto.CVSRepositoryDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.CVSRepositoryServiceDTO;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class CVSRepositoryControllerImpl extends BaseControllerImpl<CVSRepositoryDTO> implements CVSRepositoryController {

    @Autowired
    private CVSRepositoryServiceDTO cvsRepositoryService;

    @Override
    protected TTriageService<CVSRepositoryDTO> getService() {
        return cvsRepositoryService;
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getCvsRepositoriesNames() {
        return ResponseEntity.ok(cvsRepositoryService.getCvsRepositoriesNames());
    }
}
