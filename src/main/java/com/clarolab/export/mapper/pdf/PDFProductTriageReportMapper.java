/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.mapper.pdf;

import com.clarolab.dto.ProductDTO;
import com.clarolab.export.mapper.ProductTriageReportMapper;
import com.clarolab.model.*;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.ProductServiceDTO;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.GroupedStatView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PDFProductTriageReportMapper extends AbstractPdfMapper implements PDFMapper, ProductTriageReportMapper {


    @Autowired
    private StatsService statsService;

    @Autowired
    private ProductServiceDTO productServiceDTO;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private IssueTicketService issueTicketService;

    @Autowired
    private TestTriageService testTriageService;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private ApplicationDomainService applicationDomainService;    

    private Image tick;
    private Image bug;
    private Image pend;

    @Override
    public void createPDFDocument(PdfWriter writer, Document document, Map<String, Object> model) throws Exception {

        ProductDTO content = (ProductDTO) model.get("content");
        Product product = productServiceDTO.convertToEntity(content);

        Long startDate = DateUtils.beginDay(-7);
        Long endDate = DateUtils.now();

        addReportTitle(document, "Automation Report", product.getName());

        Image logo = getImage("ttriage_logo.png", Image.MIDDLE, 32, 32);
        tick = getImage("green_tick.png", Image.ALIGN_CENTER, 12, 12);
        bug = getImage("bug_report_black.png", Image.ALIGN_CENTER | Image.ALIGN_MIDDLE, 12, 12);
        pend = getImage("black_pending.png", Image.ALIGN_CENTER, 12, 12);

        addParagraph(document, product.getName() + " Automation Report", Element.ALIGN_LEFT, font12ItaBlack);
        addReportLogo(document, logo);

        addLine(document);
        addParagraph(document, "From: " + DateUtils.covertToString(startDate, "yyyy-MM-dd") + " to: " + DateUtils.covertToString(endDate, "yyyy-MM-dd"), Element.ALIGN_RIGHT, font9DarkGray);

        addNewLine(document);


        GroupedStatView productStat = statsService.getProductStat(product, startDate, endDate);
        long tests = productStat.getTotal();
        long toTriage = productStat.getToTriage();
        long commits = productService.countCvsLogsByProductIdAndTimestamp(product, true, startDate, endDate);

        Image bug = getImage("bug_report_black.png", Image.ALIGN_CENTER | Image.ALIGN_MIDDLE, 12, 12);
        Image finger = getImage("fingerprint_black.png", Image.ALIGN_CENTER | Image.ALIGN_MIDDLE, 12, 12);

        addParagraph(document, "SUMMARY:", font12BoldDarkGray);
        addNewLine(document);
        addImageAndText(document, finger, 1, "Total amount of owned tests executed: " + tests, Element.ALIGN_LEFT, font9Black);
        addImageAndText(document, bug, 1, "Total amount of automated tests fixed in the period: " + toTriage, Element.ALIGN_LEFT, font9Black);
        addImageAndText(document, tick, 1, "Total amount of commits performed: " + commits, Element.ALIGN_LEFT, font9Black);

        addNewLine(document);
        addParagraph(document, "PRODUCT TEST SUITES: " + product.getName(), font12BoldDarkGray);
        addTriagedSuiteSection(document, product, startDate, endDate);
        addPendingSuiteSection(document, product, startDate, endDate);
        addProductIssuesSection(document, product, startDate, endDate);
        addAutomationIssuesSection(document, product, startDate, endDate);
        addUserTriageSection(document, product, startDate, endDate);

        addFooterDateTime(writer, font8DarkGray);
    }

    private void addUserTriageSection(Document document, Product product, Long startDate, Long endDate) throws DocumentException {
        addParagraph(document, "USERS STATUS", font12BoldDarkGray);
        addParagraph(document, "List of suites triaged by user:", font9DarkGray);

        Map<User, List<BuildTriage>> userTriages = buildTriageService.getBuildTriageByGroupedByUser(product, true, startDate, endDate);

        if (userTriages.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, pend, 1, "No suites triaged in this period", Element.ALIGN_LEFT, font9DarkGray);
        } else {
            userTriages.forEach((user, buildTriages) -> {
                try {
                    addNewLine(document);
                    addParagraph(document, "Engineer:" + user.getRealname(), Element.ALIGN_LEFT, font9ItaBlack);
                    addParagraph(document, "Test Suites triaged by " + user.getRealname(), font9DarkGray);
                    addUserTriagedSuitesTable(document, buildTriages);

                    List<GroupedStatView> suitesPendingToTriaged = statsService.getSuitesPendingToTriaged(product, user, startDate, endDate);  // I need more stats, thats why i am using statsService
                    addUserPendingSuitesTable(document, suitesPendingToTriaged);

                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                }

            });
        }

        addNewLine(document);
    }

    private void addAutomationIssuesSection(Document document, Product product, Long startDate,  Long endDate) throws DocumentException, IOException {
        addParagraph(document, "CRITICAL AUTOMATION ISSUES", font12BoldDarkGray);
        addParagraph(document, "List of automated tests that are failing and are blockers for the product:", font9DarkGray);

        List<AutomatedTestIssue> automationIssues = automatedTestIssueService.getAutomationIssues(product, startDate, endDate);

        if (automationIssues.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, bug,1, "No automation issues filed in this period", Element.ALIGN_LEFT, font9DarkGray);
        } else {
            createHeader(headerNamesAutomationIssues);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{25f, 35f, 15f, 10f, 15f};
            table.setWidths(columnWidths);
            //"Test", "Suite", "Priority", "Status", "Filed"
            for (AutomatedTestIssue ati : automationIssues) {
                table.addCell(getPdfCell(ati.getTestCaseName(), font9DarkGray));
                table.addCell(getPdfCell(ati.getExecutorName(), font9DarkGray));
                table.addCell(getPdfCell(ati.getUserFixPriorityType().name(), font9DarkGray));
                table.addCell(getPdfCell(ati.getIssueType().name(), font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(ati.getTimestamp()), font9DarkGray));

            }
            document.add(table);

            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/SuiteList");
            addLink(document, link);
        }
        addNewLine(document);
    }

    private void addProductIssuesSection(Document document, Product product, Long startDate,  Long endDate) throws DocumentException, IOException {
        addParagraph(document, "OPEN PRODUCT ISSUES", font12BoldDarkGray);
        addParagraph(document, "List of open issues with tests that have been executed recently:", font9DarkGray);

        List<IssueTicket> productIssues = issueTicketService.getProductIssues(product, startDate, endDate);

        if (productIssues.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, bug,1, "No issues filed in this period", Element.ALIGN_LEFT, font9DarkGray);
        } else {
            createHeader(headerNamesProductIssues);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{25f, 38f, 12f, 10f, 15f};
            table.setWidths(columnWidths);
            //"Ticket", "Summary", "Assignee", "Status", "Filed"
            for (IssueTicket it : productIssues) {
                table.addCell(getPdfCell(it.getUrl(), font9DarkGray));
                table.addCell(getPdfCell(it.getSummary(), font9DarkGray));
                table.addCell(getPdfCell(it.getComponent(), font9DarkGray));
                table.addCell(getPdfCell(it.getIssueType().name(), font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(it.getTimestamp()), font9DarkGray));

                // addImageAndText(document, tick, 1, format("%s: %s: %s", gsv.getProductName(), gsv.getContainerName(), gsv.getName()), Element.ALIGN_LEFT, font9);
            }
            document.add(table);
            
            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/SuiteList");
            addLink(document, link);            
        }
        addNewLine(document);
    }

    private void addTriagedSuiteSection(Document document, Product product, Long startDate,  Long endDate) throws DocumentException, IOException {
        addParagraph(document, "The following test suites have been triaged:", font9DarkGray);

        List<BuildTriage> suitesTriaged = buildTriageService.getBuildTriageBy(product, true, startDate, endDate);

        if (suitesTriaged.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, tick,1, "No suites triaged in this period", Element.ALIGN_LEFT, font9DarkGray);
        } else {
            createHeader(headerNamesTriaged);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{30f, 45f, 10f, 15f};
            table.setWidths(columnWidths);
            //"Container", "Suite Name", "Tests", "Date"
            for (BuildTriage bt : suitesTriaged) {

                long tests = testTriageService.countBy(bt.getBuild(), true);

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

    private void addPendingSuiteSection(Document document, Product product, Long startDate,  Long endDate) throws DocumentException, IOException {
        addParagraph(document, "The following suites have pending tests to triage:", font9DarkGray);

        List<GroupedStatView> suitesPending = statsService.getSuitesPendingToTriaged(product, startDate, endDate); // I need more stats, thats why i am using statsService

        if (suitesPending.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, pend,1, "No suites pending to triage in this period", Element.ALIGN_LEFT, font9DarkGray);
        } else {
            createHeader(headerNamesPending);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{18f, 23f, 8f, 8f, 8f, 11f, 10f, 14f};
            table.setWidths(columnWidths);
            //"Container", "Suite Name", "New Fail", "Fail", "New Pass", "Triaged", "Total", "Date"
            for (GroupedStatView gsv : suitesPending) {
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

    private void addUserTriagedSuitesTable(Document document, List<BuildTriage> buildTriages) throws DocumentException, IOException{

        if (buildTriages.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, tick,1, "No suites triaged in this period", Element.ALIGN_LEFT, font9DarkGray);

        } else {
            createHeader(headerNamesTriaged);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{30f, 45f, 10f, 15f};
            table.setWidths(columnWidths);
            //"Product", "Container", "Suite Name", "Tests", "Date"
            for (BuildTriage bt : buildTriages) {

                long tests = testTriageService.countBy(bt.getBuild(), true);

                table.addCell(getPdfCell(bt.getContainerName(), font9DarkGray));
                table.addCell(getPdfCell(bt.getExecutorName(), font9DarkGray));
                table.addCell(getPdfCell(tests, font9DarkGray));
                table.addCell(getPdfCell(getNormalizedDateTime(bt.getUpdated()), font9DarkGray));

            }
            document.add(table);

            // Add link to t-Triage Application.
            String link = StringUtils.concatURL(applicationDomainService.getURL(), "/SuiteList");
            addLink(document, link);
        }
        addNewLine(document);
    }

    private void addUserPendingSuitesTable(Document document, List<GroupedStatView> suitesPending) throws DocumentException, IOException {
        addParagraph(document, "The following suites have pending tests to triage:", font9DarkGray);

        if (suitesPending.isEmpty()) {
            addNewLine(document);
            addImageAndText(document, pend, 1, "No suites pending to triage in this period", Element.ALIGN_LEFT, font9DarkGray);
            addNewLine(document);
        } else {
            createHeader(headerNamesPending);

            PdfPTable table = getTable();
            float[] columnWidths = new float[]{20f, 21f, 8f, 8f, 8f, 11f, 10f, 14f};
            table.setWidths(columnWidths);
            //"Container", "Suite Name", "New Fail", "Fail", "New Pass", "Triaged", "Total", "Date"
            for (GroupedStatView gsv : suitesPending) {
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
    }
}
