package com.clarolab.controller.impl;

import com.clarolab.controller.LicenseController;
import com.clarolab.dto.LicenceStatusDTO;
import com.clarolab.dto.LicenseDTO;
import com.clarolab.service.LicenseService;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.LicenseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class LicenseControllerImpl extends BaseControllerImpl<LicenseDTO> implements LicenseController {

    @Autowired
    private LicenseServiceDTO licenseServiceDTO;

    @Autowired
    private LicenseService licenseService;

    @Override
    protected TTriageService<LicenseDTO> getService() {
        return licenseServiceDTO;
    }

    @Override
    public ResponseEntity<LicenseDTO> getLicense() {
        return ResponseEntity.ok(licenseServiceDTO.getLicense());
    }

    @Override
    public ResponseEntity<LicenceStatusDTO> getLicenseStatus() {
        return ResponseEntity.ok(licenseService.getLicenseStatus());
    }

    @Override
    public ResponseEntity<Boolean> checkLicenceExpiry() {
        return ResponseEntity.ok(licenseService.checkLicenseExpiry());
    }


}
