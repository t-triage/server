/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.xls;

import com.clarolab.dto.UserDTO;
import com.clarolab.export.mapper.UserListMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class XLSUserListMapper extends AbstractXLSMapper implements XLSMapper, UserListMapper {

    @Override
    public void createXLSDocument(Map<String, Object> model, Workbook workbook) throws Exception {
        List<UserDTO> users = (List<UserDTO>) model.get("content");

        Sheet sheet = createSheet(workbook, mapperType());

        createHeader(workbook, sheet, headerNames);

        //  create content rows
        int rowCount = 1;
        for (UserDTO user : users) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(user.getUsername());
            row.createCell(1).setCellValue(user.getRealname());
            row.createCell(2).setCellValue(user.getDisplayName());
        }
    }
}
