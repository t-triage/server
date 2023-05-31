/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper;

import com.clarolab.util.StringUtils;

public interface AbstractExportMapper {

    String mapperType();

    default String[] getHeaders(String[] headers){
        String[] newHeaders = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            newHeaders[i] = StringUtils.trimAll(headers[i]);

        }
        return newHeaders;
    }
}
