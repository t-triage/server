/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ReportDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ReportMapper;
import com.clarolab.model.Build;
import com.clarolab.model.Report;
import com.clarolab.service.ReportService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportServiceDTO implements BaseServiceDTO<Report, ReportDTO, ReportMapper> {

    @Autowired
    private ReportService service;

    @Autowired
    private ReportMapper mapper;

    @Override
    public TTriageService<Report> getService() {
        return service;
    }

    @Override
    public Mapper<Report, ReportDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Report, ReportDTO, ReportMapper> getServiceDTO() {
        return this;
    }

  /*  public List<ReportDTO> convertToDTONoExecutions(List<Report> reportHistory) {
        List<ReportDTO> answer = new ArrayList<>(reportHistory.size());
        for (Report entity : reportHistory) {
            answer.add(mapper.convertToDTOSimple(entity));
        }
        return answer;
    }*/

    public List<ReportDTO> convertToDTONoExecutions(List<Build> buildHistory) {
        List<ReportDTO> answer = new ArrayList<>(buildHistory.size());
        for (Build entity : buildHistory) {
            answer.add(mapper.convertToDTOSimple(entity));
        }
        return answer;
    }
}
