/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Report;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ReportRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class ReportService extends BaseService<Report> {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public BaseRepository<Report> getRepository() {
        return reportRepository;
    }
}
