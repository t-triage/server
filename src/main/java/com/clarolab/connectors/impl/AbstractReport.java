package com.clarolab.connectors.impl;

import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractReport {

    @Getter
    @Setter
    private ApplicationContextService context;

    public abstract Report createReport(String json, String observations) throws ReportServiceException;
}
