/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.pdf;

import com.clarolab.export.mapper.pdf.PDFMapper;
import com.clarolab.export.mapper.pdf.UndefinedPDFMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PdfView extends AbstractPdfView {

    private Map<String, PDFMapper> mapperMap;

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // change the file name
        response.addHeader("Content-Disposition", "attachment; filename=\"report_" + LocalDate.now() + ".pdf\"");
        String type = (String) model.get("type");
        PDFMapper mapper = mapperMap.getOrDefault(type, new UndefinedPDFMapper());
        mapper.createPDFDocument(writer, document, model);
    }

    @Autowired
    public void setMapperMap(List<PDFMapper> mapperMap) {
        this.mapperMap = mapperMap.stream().collect(Collectors.toMap(PDFMapper::mapperType, Function.identity()));
    }
}
