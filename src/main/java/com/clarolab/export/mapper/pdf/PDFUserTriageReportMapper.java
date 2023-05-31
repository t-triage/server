/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.clarolab.export.mapper.UserTriageReportMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.User;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.GroupedStatView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PDFUserTriageReportMapper extends AbstractPdfMapper implements PDFMapper, UserTriageReportMapper {


    @Autowired
    private StatsService statsService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    private Image pend, tick;

    @Override
    public void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception {

        User user = (User) model.get("content");

        Long startDate = DateUtils.beginDay(-7);
        Long endDate = DateUtils.now();

        writer.setStrictImageSequence(true);

        addReportTitle(document, "Automation Report", user.getRealname());

        Image logo = getImage("ttriage_logo.png", Image.MIDDLE, 32, 32);
        Image bug = getImage("bug_report_black.png", Image.ALIGN_CENTER | Image.ALIGN_MIDDLE, 12, 12);
        Image finger = getImage("fingerprint_black.png", Image.ALIGN_CENTER | Image.ALIGN_MIDDLE, 12, 12);
        pend = getImage("black_pending.png", Image.ALIGN_CENTER, 12, 12);
        tick = getImage("green_tick.png", Image.ALIGN_CENTER, 12, 12);

        addParagraph(document, user.getRealname() + " Automation Report", Element.ALIGN_LEFT, font12ItaBlack);
        addReportLogo(document, logo);

        addLine(document);
        addParagraph(document, "From: " + DateUtils.covertToString(startDate, "yyyy-MM-dd") + " to: " + DateUtils.covertToString(endDate, "yyyy-MM-dd"), Element.ALIGN_RIGHT, font9DarkGray);
        addNewLine(document);

        long tests = statsService.testCountAssignedTo(user, startDate, endDate);
        long fixes = statsService.countAllButFixed(user, startDate, endDate);
        long commits = statsService.countCommitsPerPerson(user, startDate, endDate);

        addParagraph(document, "SUMMARY:", font12BoldDarkGray);
        addNewLine(document);

        addImageAndText(document, finger, 1, "Total amount of owned tests executed: " + tests, Element.ALIGN_LEFT, font9Black);
        addImageAndText(document, bug, 1, "Total amount of automated tests fixed in the period: " + fixes, Element.ALIGN_LEFT, font9Black);
        addImageAndText(document, tick, 1, "Total amount of commits performed: " + commits, Element.ALIGN_LEFT, font9Black);

        addNewLine(document);
        addParagraph(document, "OWNED TEST SUITES:", font12BoldDarkGray);
        addTriagedSuiteSection(document, user, startDate, endDate);
        addPendingSuiteSection(document, user, startDate, endDate);
        addPendingSuitePieChart(writer, document, user, startDate, endDate);

        addNewLine(document);
        addParagraph(document, "PENDING TO FIX:", font12BoldDarkGray);
        addPendingFixSection(document, user, startDate, endDate);

        addFooterDateTime(writer, font8DarkGray);
    }

    private void addPendingSuitePieChart(PdfWriter writer, Document document, User user, Long startDate, Long endDate) throws DocumentException {

        GroupedStatView suitesPending = statsService.getCountTestsSummary(user, startDate, endDate);

        DefaultPieDataset myPiedataset = new DefaultPieDataset();
        myPiedataset.setValue("Fail", suitesPending.getFails());
        myPiedataset.setValue("Triaged", suitesPending.getTriaged());
        myPiedataset.setValue("Pass", suitesPending.getNowPassing());

        addPieChart(writer, document, "Pending Work", myPiedataset, 320, 240, Image.MIDDLE);
    }

    private void addTriagedSuiteSection(Document document, User user, Long startDate, Long endDate) throws DocumentException, IOException {
        addParagraph(document, "The following test suites have been triaged:", font9DarkGray);

        List<BuildTriage> suitesTriaged = buildTriageService.getBuildTriageBy(user, true, startDate, endDate);
        if (suitesTriaged.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, tick, 1, "No suites triaged in this period", Element.ALIGN_LEFT, font9DarkGray);

        } else {
            createHeader(headerNamesTriaged);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{20f, 25f, 30f, 10f, 15f};
            table.setWidths(columnWidths);
            //"Product", "Container", "Suite Name", "Tests", "Date"
            for (BuildTriage bt : suitesTriaged) {

                long tests = testTriageService.countBy(bt.getBuild(), true);

                table.addCell(getPdfCell(bt.getProductName(), font9DarkGray));
                table.addCell(getPdfCell(bt.getContainerName(), font9DarkGray));
                table.addCell(getPdfCell(bt.getExecutorName(), font9DarkGray));
                table.addCell(getPdfCell(tests, font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(bt.getTimestamp()), font9DarkGray));
            }
            document.add(table);

            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/SuiteList");
            addLink(document, link);
        }
        addNewLine(document);
    }

    private void addPendingSuiteSection(Document document, User user, Long startDate, Long endDate) throws DocumentException, IOException {
        addParagraph(document, "The following suites have pending tests to triage:", font9DarkGray);

        List<GroupedStatView> suitesPending = statsService.getSuitesPendingToTriaged(user, startDate, endDate);  // I need more stats, thats why i am using statsService

        if (suitesPending.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, pend, 1, "No suites pending to triage in this period", Element.ALIGN_LEFT, font9DarkGray);
            addNewLine(document);
        } else {
            createHeader(headerNamesPending);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{13f, 15f, 15f, 7f, 8f, 7f, 11f, 10f, 14f};
            table.setWidths(columnWidths);
            //"Product", "Container", "Suite Name", "New Fail", "Fail", "New Pass", "Triaged", "Total", "Date"
            for (GroupedStatView gsv : suitesPending) {
                table.addCell(getPdfCell(gsv.getProductName(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getContainerName(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getName(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getNewFails(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getFails(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getNowPassing(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getTriaged(), font9DarkGray));
                table.addCell(getPdfCell(gsv.getTotal(), font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(gsv.getTimestamp()), font9DarkGray));

            }
            document.add(table);

            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/SuiteList");
            addLink(document, link);
        }
        addNewLine(document);
    }

    private void addPendingFixSection(Document document, User user, Long startDate, Long endDate) throws DocumentException, IOException {
        addParagraph(document, "The following test suites are pending to fix:", font9DarkGray);

        List<AutomatedTestIssue> fixed = automatedTestIssueService.getAutomationIssues(user, startDate, endDate);
        if (fixed.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, pend, 1, "No pending to fix in this period", Element.ALIGN_LEFT, font9DarkGray);
            addNewLine(document);
        } else {
            createHeader(headerNamesFixed);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{28f, 12f, 15f, 7f, 13f, 11f, 14f};
            table.setWidths(columnWidths);

            //"Test", "Product", "Suite Name", "Fail", ,"Priority", "Status", "Date"
            for (AutomatedTestIssue at : fixed) {

                table.addCell(getPdfCell(at.getTestCaseName(), font9DarkGray));
                table.addCell(getPdfCell(at.getTestTriage().getProduct().getName(), font9DarkGray));
                table.addCell(getPdfCell(at.getExecutorName(), font9DarkGray));
                table.addCell(getPdfCell(at.getFailTimes(), font9DarkGray));
                table.addCell(getPdfCell(at.getUserFixPriorityType().name(), font9DarkGray));
                table.addCell(getPdfCell(at.getIssueType().name(), font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(at.getTimestamp()), font9DarkGray));
            }
            document.add(table);

            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/AutomationIssues");
            addLink(document, link);
        }
        addNewLine(document);
    }
}
