/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.clarolab.export.mapper.AbstractExportMapper;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;


public interface PDFMapper extends AbstractExportMapper {

    void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception;

    Logger LOGGER = Logger.getLogger(PDFMapper.class.getName());

    Font font8DarkGray = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.DARK_GRAY);
    Font font9DarkGray = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.DARK_GRAY);
    Font font9Black = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    Font font9ItaBlack = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 9, BaseColor.BLACK);
    Font font9BoldDarkGray = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.DARK_GRAY);
    Font font10BoldBlack = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    Font font12BoldDarkGray = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
    Font font12ItaBlack = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.BLACK);


    PdfPCell header = new PdfPCell();
    PdfPTable table = new PdfPTable(1);

    default void addReportLegend(Document document) throws DocumentException {
        addParagraph(document, "Generated Report " + LocalDate.now());
    }

    default void addLink(Document document, String link) throws DocumentException {
        Paragraph paragraph = new Paragraph("Link to ",font9DarkGray);

        Anchor anchor = new Anchor("t-Triage", font9BoldDarkGray);
        anchor.setReference(link);
        paragraph.add(anchor);
        document.add(paragraph);        
    }

    default void addParagraph(Document document, String text) throws DocumentException {
        addParagraph(document, text, Element.ALIGN_LEFT, font10BoldBlack);
    }

    default void addNewLine(Document document) throws DocumentException {
        addNewLine(document, 1);
    }

    default void addNewLine(Document document, int lines) throws DocumentException {
        for (int i = 0; i < lines; i++) {
            document.add(new Paragraph(" "));
        }
    }

    default void addParagraph(Document document, String text, Font font) throws DocumentException {
        addParagraph(document, text, Element.ALIGN_LEFT, font);
    }

    default void addParagraph(Document document, String text, int alignment, Font font) throws DocumentException {
        addParagraph(document, text, 0 , alignment, font);
    }

    default void addParagraph(Document document, String text, int tab, int alignment, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        for (int i=0; i < tab; i++)
            paragraph.add(Chunk.TABBING);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    default void addImage(Document document, String name, int width, int height) throws DocumentException {
        addImage(document, name, Element.ALIGN_LEFT, width, height);
    }

    default void addImageAndText(Document document, Image image, String text, int alignment, Font font) throws DocumentException {
        addImageAndText(document, image, 0, text, alignment, font);
    }

    default void addImageAndText(Document document, Image image, int tab, String text, int alignment, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph("", font);
        for (int i = 0; i < tab; i++)
            paragraph.add(Chunk.TABBING);
        paragraph.add(new Chunk(image, 0, 0));
        paragraph.add(text);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    default void addImage(Document document, String name, int alignment, int width, int height) throws DocumentException {
        try {
            Image image = getImage(name, alignment, width, height);
            document.add(image);
        }catch (IOException e){
            //Nothing to do here
        }
    }

    default Image getImage(String name, int alignment, int width, int height) throws BadElementException, IOException {
        InputStream inputStream = getClass().getResourceAsStream("/img/" + name);
        BufferedImage bf = ImageIO.read(inputStream);
        Image image = Image.getInstance(bf, null);
        image.scaleAbsolute(width, height);
        image.setAlignment(alignment);
        return image;
    }

    default void addLine(Document document) throws DocumentException {
        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));
    }

    default void addReportTitle(Document document, String title, String author) throws DocumentException {
        document.addCreationDate();
        document.addCreator("tTriage Report");
        document.addAuthor(author);
        document.addSubject("tTriage Report - " +title);
        document.addTitle(title);
    }

    default PdfPTable getTable() {
        return table;
    }

    default void addColumn(String... name) {
        Arrays.asList(name).forEach(s -> {
                    header.setPhrase(new Phrase(s.toUpperCase(), font9BoldDarkGray));
                    table.addCell(header);
                }
        );
    }

    default void addReportLogo(Document document, Image image) throws DocumentException{
        float x = PageSize.A4.getWidth() - (image.getScaledWidth() ) - 35 ;
        float y = PageSize.A4.getHeight() - (image.getScaledWidth() ) - 50 ;
        image.setAbsolutePosition(x, y);
        document.add(image);
    }

    default void addPieChart(PdfWriter writer, Document document, String title, DefaultPieDataset myPiedataset, int width, int height, int alignment) {
        //int width=320; /* Width of our chart */
        //int height=240; /* Height of our chart */

        try {

            JFreeChart chart = ChartFactory.createPieChart(title, myPiedataset, true, false, false);

            TextTitle textTitle = new TextTitle(title, new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 10));
            chart.setTitle(textTitle);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(null);
            LegendTitle legend = chart.getLegend();
            legend.setItemFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 9));
            PdfContentByte contentByte = writer.getDirectContent();
            PdfTemplate template = contentByte.createTemplate(width, height);
            Graphics2D graphics2d = new PdfGraphics2D(template, width, height, new DefaultFontMapper());
            Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
            Color color = Color.WHITE;
            chart.setBackgroundPaint(color);
            chart.setBorderPaint(color);
            chart.getPlot().setBackgroundPaint(color);
            chart.getPlot().setOutlinePaint(color);
            chart.draw(graphics2d, rectangle2d);
            graphics2d.dispose();
            Image imagePie = Image.getInstance(template);

            imagePie.setAlignment(alignment);
            document.add(imagePie);

        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Unable to add Pie Chart: " + e.getMessage(), e);
        }
    }

    default void createHeader(String[] headerNames) throws DocumentException {

        table.deleteBodyRows();

        table.setWidthPercentage(100.0f);
        table.setSpacingBefore(10);

        // define font10RegularWhite for table header row

        // define table header header
        header.setBackgroundColor(new BaseColor(235, 235, 235));
        header.setPadding(5);

        // write table header
        table.resetColumnCount(headerNames.length);
        addColumn(headerNames);
    }

    default void addFooterLegend(PdfWriter writer, String text, Font font) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(text, font), 90, 30, 0);
    }

    default String getNormalizedDateTime(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(time);
    }

    default String getNormalizedDateTime(long time){
        LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
        return getNormalizedDateTime(triggerTime);
    }

    default void addFooterDateTime(PdfWriter writer, Font font) {
        addFooterLegend(writer, "Report generated at " + getNormalizedDateTime(LocalDateTime.now()), font);
    }

    default PdfPCell getPdfCell(String value, Font font) {
        return new PdfPCell(new Phrase(value, font));
    }

    default PdfPCell getPdfCell(long value, Font font) {
        return getPdfCell(String.valueOf(value), font);
    }

    default PdfPCell getPdfCell(double value, Font font) {
        return getPdfCell(String.valueOf(value), font);
    }
}

