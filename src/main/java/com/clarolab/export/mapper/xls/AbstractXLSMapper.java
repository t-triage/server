/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.xls;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

public abstract class AbstractXLSMapper implements XLSMapper {

    Sheet createSheet(Workbook workbook, String name) {
        Sheet sheet = workbook.createSheet(name);
        sheet.setDefaultColumnWidth(30);
        return sheet;
    }

    CellStyle createStyle(Workbook workbook) {
        // create style for header cells
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    void createHeader(Workbook workbook, Sheet sheet, String[] headerNames) {
        CellStyle style = createStyle(workbook);

        // create header row
        Row header = sheet.createRow(0);
        int i = 0;
        for (String s : headerNames) {
            header.createCell(i).setCellValue(s.toUpperCase());
            header.getCell(i).setCellStyle(style);
            i++;
        }
    }
}
