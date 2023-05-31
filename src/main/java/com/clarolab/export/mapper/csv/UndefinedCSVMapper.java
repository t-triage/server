/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.csv;

import org.springframework.stereotype.Component;
import org.supercsv.io.ICsvBeanWriter;

import java.util.Map;

@Component
public class UndefinedCSVMapper implements CVSMapper {

    @Override
    public String mapperType() {
        return "UNDEFINED";
    }

    @Override
    public void createCVSDocument(ICsvBeanWriter csvWriter, Map<String, Object> model) throws Exception {
        String[] header = {mapperType()};
        csvWriter.writeHeader(header);
        csvWriter.close();
    }
}
