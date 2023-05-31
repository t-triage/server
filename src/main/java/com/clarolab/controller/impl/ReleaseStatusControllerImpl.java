/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ReleaseStatusController;
import com.clarolab.service.BucketService;
import com.clarolab.service.ReleaseStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ReleaseStatusControllerImpl implements ReleaseStatusController {

    @Autowired
    private ReleaseStatusService releaseStatusService;

    @Autowired
    private BucketService bucketService;


    @Override
    public ResponseEntity<Boolean> getProductStatus(Long id) {
        return ResponseEntity.ok(releaseStatusService.getProductStatus(id));
    }

    @Override
    public ResponseEntity<Boolean> getContainerStatus(Long id) {
        return ResponseEntity.ok(releaseStatusService.getContainerStatus(id));
    }

    @Override
    public ResponseEntity<Boolean> getExecutorStatus(Long id) {
        return ResponseEntity.ok(releaseStatusService.getExecutorStatus(id));
    }

    @Override
    public ResponseEntity<Boolean> getPipelineStatus(Long id) {
        if (bucketService.getBucket().tryConsume(1)) {

            return ResponseEntity.ok(releaseStatusService.getPipelineStatus(id));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @Override
    public ResponseEntity<String> getPipelineHelp(Long id) {
        return ResponseEntity.ok(releaseStatusService.getPipelineHelp(id));
    }
}
