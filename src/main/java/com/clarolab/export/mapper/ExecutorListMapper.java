/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper;

import static com.clarolab.util.Constants.EXPORT_EXECUTORLIST;

public interface ExecutorListMapper extends AbstractExportMapper {

    String[] headerNames = {
            "executor Name",
            "executor Description",
            "execution date",
            "total Tests",
            "fail Count",
            "pass Count",
            "skip Count",
            "stability Index",
            "priority"
    };

    @Override
    default String mapperType() {
        return EXPORT_EXECUTORLIST;
    }
}
