/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.csv;


import com.clarolab.export.mapper.csv.CVSMapper;
import com.clarolab.export.mapper.csv.UndefinedCSVMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CsvView extends AbstractCsvView {

    private Map<String, CVSMapper> mapperMap;

    @Override
    protected void buildCsvDocument(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.addHeader("Content-Disposition", "attachment; filename=\"report_" + LocalDate.now() + ".csv\"");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String type = (String) model.get("type");
        CVSMapper mapper = mapperMap.getOrDefault(type, new UndefinedCSVMapper());
        mapper.createCVSDocument(csvWriter, model);
    }

    @Autowired
    public void setMapperMap(List<CVSMapper> mapperMap) {
        this.mapperMap = mapperMap.stream().collect(Collectors.toMap(CVSMapper::mapperType, Function.identity()));
    }
}

