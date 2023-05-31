/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class UndefinedPDFMapper implements PDFMapper {

    @Override
    public String mapperType() {
        return "UNDEFINED";
    }


    @Override
    public void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception {
        document.add(new Paragraph("Generated Report " + LocalDate.now()));
    }
}
