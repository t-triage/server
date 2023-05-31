/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.clarolab.dto.UserDTO;
import com.clarolab.export.mapper.UserListMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PDFUserListMapper extends AbstractPdfMapper implements PDFMapper, UserListMapper {

    @Override
    public void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception {
        List<UserDTO> content = (List<UserDTO>) model.get("content");

        createHeader(headerNames);
        PdfPTable table = getTable();

        for (UserDTO user : content) {
            table.addCell(getPdfCell(user.getUsername(), font9DarkGray));
            table.addCell(getPdfCell(user.getRealname(), font9DarkGray));
            table.addCell(getPdfCell(user.getDisplayName(), font9DarkGray));
        }

        document.add(table);
        addFooterDateTime(writer, font8DarkGray);
    }

}
