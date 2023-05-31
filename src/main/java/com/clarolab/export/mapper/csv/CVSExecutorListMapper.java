/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.csv;

import com.clarolab.export.mapper.ExecutorListMapper;
import com.clarolab.view.ExecutorView;
import org.springframework.stereotype.Component;
import org.supercsv.io.ICsvBeanWriter;

import java.util.List;
import java.util.Map;

@Component
public class CVSExecutorListMapper implements CVSMapper, ExecutorListMapper {

    @Override
    public void createCVSDocument(ICsvBeanWriter csvWriter, Map<String, Object> model) throws Exception {
        List<ExecutorView> content = (List<ExecutorView>) model.get("content");


        csvWriter.writeHeader(headerNames);

        for (ExecutorView executor : content) {
            csvWriter.write(executor, getHeaders(headerNames));
        }
        csvWriter.close();
    }
}
