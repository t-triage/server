/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.xls;

import com.clarolab.export.mapper.ExecutorListMapper;
import com.clarolab.view.ExecutorView;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class XLSExecutorListMapper extends AbstractXLSMapper implements XLSMapper, ExecutorListMapper {

    @Override
    public void createXLSDocument(Map<String, Object> model, Workbook workbook) throws Exception {
        List<ExecutorView> executors = (List<ExecutorView>) model.get("content");

        Sheet sheet = createSheet(workbook, mapperType());

        createHeader(workbook, sheet, headerNames);

        //  create content rows
        int rowCount = 1;
        for (ExecutorView executorView : executors) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(executorView.getExecutorName());
            row.createCell(1).setCellValue(executorView.getExecutorDescription());
            row.createCell(2).setCellValue(executorView.getExecutiondate());
            row.createCell(3).setCellValue(executorView.getTotalTests());
            row.createCell(4).setCellValue(executorView.getFailCount());
            row.createCell(5).setCellValue(executorView.getPassCount());
            row.createCell(6).setCellValue(executorView.getSkipCount());
            row.createCell(7).setCellValue(executorView.getStabilityIndex());
            row.createCell(8).setCellValue(executorView.getPriority());

        }
    }


}
