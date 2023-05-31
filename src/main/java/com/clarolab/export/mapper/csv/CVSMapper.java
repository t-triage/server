/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.csv;

import com.clarolab.export.mapper.AbstractExportMapper;
import org.supercsv.io.ICsvBeanWriter;

import java.util.Map;

public interface CVSMapper extends AbstractExportMapper {

    void createCVSDocument(ICsvBeanWriter writter, Map<String, Object> model) throws Exception;



}
