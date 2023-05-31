/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.xls;

import com.clarolab.export.mapper.AbstractExportMapper;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface XLSMapper extends AbstractExportMapper {

    void createXLSDocument(Map<String, Object> model, Workbook workbook) throws Exception;
}
