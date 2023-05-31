/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper;

import static com.clarolab.util.Constants.EXPORT_USERLIST;

public interface UserListMapper extends AbstractExportMapper {

    String[] headerNames = {
            "user name",
            "real name",
            "display Name"
    };

    @Override
    default String mapperType() {
        return EXPORT_USERLIST;
    }
}
