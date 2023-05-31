/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.clarolab.export.mapper.ExecutorListMapper;
import com.clarolab.view.ExecutorView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PDFExecutorListMapper extends AbstractPdfMapper implements PDFMapper, ExecutorListMapper {



    @Override
    public void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception {
        List<ExecutorView> executors = (List<ExecutorView>) model.get("content");


        createHeader(headerNames);
        PdfPTable table = getTable();

        float[] columnWidths = new float[]{20f, 30f, 5f, 5f, 5f, 5f, 5f, 5f, 5f};
        table.setWidths(columnWidths);
        for (ExecutorView executorView : executors) {

            table.addCell(getPdfCell(executorView.getExecutorName(), font9DarkGray));

            table.addCell(getPdfCell(executorView.getExecutorDescription(), font9DarkGray));
            table.addCell(getPdfCell(executorView.getExecutiondate(), font9DarkGray));

            table.addCell(getPdfCell(executorView.getTotalTests(), font9DarkGray));
            table.addCell(getPdfCell(executorView.getFailCount(), font9DarkGray));
            table.addCell(getPdfCell(executorView.getPassCount(), font9DarkGray));
            table.addCell(getPdfCell(executorView.getSkipCount(), font9DarkGray));

            table.addCell(getPdfCell(executorView.getStabilityIndex(), font9DarkGray));
            table.addCell(getPdfCell(executorView.getPriority(), font9DarkGray));

        }

        document.add(table);

        addFooterDateTime(writer, font8DarkGray);
    }

    private PdfPCell getPdfCell(String value) {
        return new PdfPCell(new Phrase(value, font9DarkGray));
    }

    private PdfPCell getPdfCell(long value) {
        return getPdfCell(String.valueOf(value));
    }

    private PdfPCell getPdfCell(double value) {
        return getPdfCell(String.valueOf(value));
    }


}
