/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper;

import static com.clarolab.util.Constants.EXPORT_USERREPORT;

public interface UserTriageReportMapper extends AbstractExportMapper {

    String[] headerNamesTriaged = {"Product", "Container", "Suite Name", "Tests", "Date"};
    String[] headerNamesPending = {"Product", "Container", "Suite Name", "New Fail", "Fail", "New Pass", "Triaged", "Total", "Deadline"};
    String[] headerNamesFixed = {"Test", "Product", "Suite Name", "Fail","Priority", "Status", "Date"};

    @Override
    default String mapperType() {
        return EXPORT_USERREPORT;
    }
}
