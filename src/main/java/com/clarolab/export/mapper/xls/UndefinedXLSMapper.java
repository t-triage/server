/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.xls;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UndefinedXLSMapper implements XLSMapper {

    @Override
    public String mapperType() {
        return "UNDEFINED";
    }

    @Override
    public void createXLSDocument(Map<String, Object> model, Workbook workbook) throws Exception {
        workbook.createSheet(mapperType());
    }
}
