/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper;

import static com.clarolab.util.Constants.EXPORT_PRODUCTREPORT;

public interface ProductTriageReportMapper extends AbstractExportMapper {

    String[] headerNamesTriaged = {"Container", "Suite", "Tests", "Date"};
    String[] headerNamesPending = {"Container", "Suite", "New Fail", "Fail", "New Pass", "Triaged", "Total", "Deadline"};

    String[] headerNamesProductIssues = {"Ticket", "Summary", "Assignee", "Status", "Filed"};
    String[] headerNamesAutomationIssues = {"Test", "Suite", "Priority", "Status", "Filed"};


    @Override
    default String mapperType() {
        return EXPORT_PRODUCTREPORT;
    }
}
