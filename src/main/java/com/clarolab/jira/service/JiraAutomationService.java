package com.clarolab.jira.service;

import com.clarolab.jira.model.DashboardNumeration;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.model.*;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.types.IssueType;
import com.clarolab.service.ProductService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.logging.Level;

import static com.clarolab.util.Constants.PASSING_TO_FIX_AMOUNT;

@Log
@Service
public class JiraAutomationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private JiraObjectService jiraObjectService;


    public void createJiraTicket(TestExecution testExecution, TestTriage testTriage){
        JiraConfig jiraConfig = jiraConfigService.findByProduct(testExecution.getTestCase().getProduct());
        if (jiraConfig != null){
            if (testExecution.getTestCase().getRelatedIssueID() == null){
                String ticketId = createTicketWithTestExecution(jiraConfig, testExecution, testTriage);
                TestCase testCase = testExecution.getTestCase();
                testCase.setRelatedIssueID(ticketId);
                testCaseService.update(testCase);
                log.log(Level.INFO,"Jira Ticket created and saved in DB, id: "+ ticketId);
            }else {
                log.log(Level.INFO,"Jira Ticket already exists :"+testExecution.getTestCase().getRelatedIssueID());
                if (testTriage.getAutomationIssue() != null) {
//                    checkComment(jiraConfig, testTriage.getAutomationIssue());
                }else {
                    log.log(Level.INFO,"AutomationIssue not found.");
                }
            }
        }else {
            log.log(Level.INFO,"Jira config is not set for this Product ID: "+testExecution.getTestCase().getProduct().getId().toString());
        }
    }

    private String createTicketWithTestExecution(JiraConfig jiraConfig, TestExecution testExecution, TestTriage testTriage){

        String ticketID = "";
        String errorTitle = "Automation "+testExecution.getStatus()+" - "+testExecution.getDisplayName() ;
        String errorDetails = "h5. Test Status: "+testExecution.getStatus();
        if (testExecution.getStatus().toString().equals("FAIL")){
            if (testExecution.getTestCase().getAutomatedTestIssue() != null){
                if (testExecution.getTestCase().getAutomatedTestIssue().getTestTriage() != null){
                    errorDetails = errorDetails+"\n" + "_Status Description:_"+" Failed and needs triage.";
                }
            }else{
                errorDetails = errorDetails+"\n" + "_Status Description:_"+" Failed and First triage is needed";
            }
        }

        if (testTriage != null){
            Date executionDate = new Date(testTriage.getExecutionDate());
            errorDetails += "\n" +"_Executed:_ "+executionDate;
            errorDetails += "\n" + "_At:_ " + "[Content Regression Tests|" +testTriage.getExecutorName()+"/SuiteList/Container/"+ testTriage.getContainer().getId()+ "]";
            errorDetails += "\n" + "_In:_ [" + testTriage.getExecutorName()+"|" +testTriage.getBuildUrl() + "]";
            errorDetails += "\n" + "_Build:_ " + testTriage.getBuildNumber();
            errorDetails += "\n";
            errorDetails += "\n"+"h5. t-Triage:" ;
            if (testTriage.getTriager().getDisplayName() != null){
                errorDetails += "\n"+"_Owner:_ "+testTriage.getTriager().getDisplayName();
            }
            if (testTriage.getNote() != null){
                if (testTriage.getNote().getDescription() != null){
                    errorDetails += "\n"+"_Note:_ "+testTriage.getNote().getDescription();
                }
            }
        }


        if (testExecution.getScreenshotURL() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. Logs:";
            errorDetails += "\n" + "!" + testExecution.getScreenshotURL() + "|thumbnail!";
        }
        if (testExecution.getErrorDetails() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. Details:";
            errorDetails += "\n"+"_Error:_ "+ testExecution.getErrorDetails();
        }
        if (testExecution.getErrorStackTrace() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. StackTrace:";
            errorDetails += "\n"+ testExecution.getErrorStackTrace();
        }


        ticketID = jiraObjectService.createJiraIssue(jiraConfig,errorTitle, errorDetails);
        return ticketID;
        }

    private String createTicketWithAutomatedTest(JiraConfig jiraConfig, AutomatedTestIssue automatedTestIssue){
        return createTicketWithAutomatedTest(jiraConfig, automatedTestIssue, null);
    }

    private String createTicketWithAutomatedTest(JiraConfig jiraConfig, AutomatedTestIssue automatedTestIssue, String oldTicketId){

        String ticketID = "";
        String errorTitle = "Automation "+automatedTestIssue.getTestTriage().getCurrentStateName()+" "+automatedTestIssue.getTestTriage().getTestExecution().getDisplayName() ;

        String errorDetails = "h5. Test Status: "+automatedTestIssue.getTestTriage().getCurrentStateName();
        if (automatedTestIssue.getTestTriage().getCurrentStateName() == "FAIL"){
            errorDetails = errorDetails+"\n" + "_Status Description:_"+" Fail and it needs a new triage.";
        }

        Date executionDate = new Date(automatedTestIssue.getTestTriage().getExecutionDate());
        errorDetails += "\n" +"_Executed:_ "+executionDate;
        errorDetails += "\n" + "_At:_ " + "[Content Regression Tests|" +automatedTestIssue.getTestTriage().getExecutorName()+"/SuiteList/Container/"+ automatedTestIssue.getTestTriage().getContainer().getId()+ "]";
        errorDetails += "\n" + "_In:_ [" + automatedTestIssue.getTestTriage().getExecutorName()+"|" +automatedTestIssue.getTestTriage().getBuildUrl() + "]";
        errorDetails += "\n" + "_Build:_ " + automatedTestIssue.getTestTriage().getBuildNumber();
        errorDetails += "\n";
        errorDetails += "\n"+"h5. t-Triage:" ;
        if (automatedTestIssue.getTestTriage().getTriager().getDisplayName() != null){
            errorDetails += "\n"+"_Owner:_ "+automatedTestIssue.getTestTriage().getTriager().getDisplayName();
        }
        if (automatedTestIssue.getTestTriage().getNote() != null){
            if (automatedTestIssue.getTestTriage().getNote().getDescription() != null){
                errorDetails += "\n"+"_Note:_ "+automatedTestIssue.getTestTriage().getNote().getDescription();
            }
        }

        if (automatedTestIssue.getTestTriage().getTestExecution().getScreenshotURL() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. Logs:";
            errorDetails += "\n" + "!" + automatedTestIssue.getTestTriage().getTestExecution().getScreenshotURL() + "|thumbnail!";
        }
        if (automatedTestIssue.getTestTriage().getTestExecution().getErrorDetails() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. Details:";
            errorDetails += "\n"+"_Error:_ "+ automatedTestIssue.getTestTriage().getTestExecution().getErrorDetails();
        }
        if (automatedTestIssue.getTestTriage().getTextExecutionStackTrace() != null){
            errorDetails += "\n";
            errorDetails += "\n"+"h5. StackTrace:";
            errorDetails += "\n"+ automatedTestIssue.getTestTriage().getTextExecutionStackTrace();
        }

        if (oldTicketId == null){
            ticketID = jiraObjectService.createJiraIssue(jiraConfig,errorTitle, errorDetails);
        }else {
            String newErrorDetails = "h4. This Test Case was previously filled in another Jira Ticket and Closed. Please refer to the ticket ID: "+ oldTicketId + " for more details."+"\n"+errorDetails;
            ticketID = jiraObjectService.createJiraIssue(jiraConfig,errorTitle, newErrorDetails);
        }

        //ticketID = jiraObjectService.createJiraIssue(jiraConfig,errorTitle, errorDetails);
        log.log(Level.INFO,"Jira Ticket created, id: "+ ticketID);


        return ticketID;
    }


    public void checkJiraConfig(AutomatedTestIssue automatedTestIssue){

        Product productID = automatedTestIssue.getProduct();
        JiraConfig jiraConfig = jiraConfigService.findByProduct(productID);

        //If jiraConfig = null, means that Jira configuration hasn't been set for this product.
        if (jiraConfig != null) {
            if (automatedTestIssue.getTestCase().getRelatedIssueID() == null) {
                //There is no ticket created for this automatedTestIssue in Jira yet.
                String ticketId = createTicketWithAutomatedTest(jiraConfig, automatedTestIssue);

                boolean response = jiraObjectService.transitionIssue(jiraConfig, ticketId, DashboardNumeration.OPEN.getColumn());
                if (response){
                    log.log(Level.INFO,"Ticket created.");
                }
                TestCase testCase = automatedTestIssue.getTestCase();
                testCase.setRelatedIssueID(ticketId);
                //Saving the new RelatedIssueID.
                testCaseService.update(testCase);
            }else{
                checkComment(jiraConfig, automatedTestIssue);
            }
        }
    }

    private void checkComment(JiraConfig jiraConfig, AutomatedTestIssue automatedTestIssue){
        String ticketId = automatedTestIssue.getRelatedIssueID();

        //There is a ticket created already.
        IssueType issueType = automatedTestIssue.getIssueType();
        if (issueType == IssueType.REOPEN){
                //There is a ticket CLOSED and needs to be RE-OPEN so a new ticket is created linked to the old one.
                String newTicketId = createTicketWithAutomatedTest(jiraConfig, automatedTestIssue, ticketId);
                boolean response = jiraObjectService.transitionIssue(jiraConfig, newTicketId, DashboardNumeration.REOPEN.getColumn());
                if (response){
                    log.log(Level.INFO,"Ticket created and linked to the old one.");
                }
                TestCase testCase = automatedTestIssue.getTestCase();
                testCase.setRelatedIssueID(newTicketId);
                //Saving the new RelatedIssueID.
                testCaseService.update(testCase);

        }

        if (issueType == IssueType.WONT_FIX){
            boolean response = jiraObjectService.transitionIssue(jiraConfig, ticketId, DashboardNumeration.CLOSED.getColumn());
            if (response){
                log.log(Level.INFO,"Ticket moved to Closed because user set it to Wont Fix.");
                Date executionDate = new Date();
                String comment = "Ticket moved to Closed because user set it to WONTFIX.";
                comment += "\n" +"_Date:_ "+executionDate;
                addComment(jiraConfig, ticketId, comment);
            }
        }

        if (issueType == IssueType.FIXED){
            if (automatedTestIssue.getConsecutivePasses() == PASSING_TO_FIX_AMOUNT){
                boolean response = jiraObjectService.transitionIssue(jiraConfig, ticketId, DashboardNumeration.CLOSED.getColumn());
                if (response){
                    log.log(Level.INFO,"Ticket moved to Closed because it passed multiple times.");
                    Date executionDate = new Date();
                    String comment = "Ticket moved to Closed because it passed multiple times.";
                    comment += "\n" +"_Date:_ "+executionDate;
                    addComment(jiraConfig, ticketId, comment);
                }
            }
        }

        if (issueType == IssueType.PASSING){
            //if consecutive passes == 1 it means is the first time that passes since it was fixed.
            if (automatedTestIssue.getConsecutivePasses() == 1){
                boolean response = jiraObjectService.transitionIssue(jiraConfig, ticketId, DashboardNumeration.RESOLVED.getColumn());
                if (response){
                    log.log(Level.INFO,"Ticket moved to Fixed for testing.");
                    Date executionDate = new Date();
                    String comment = "Ticket set to PASSING state.";
                    comment += "\n" +"_Date:_ "+executionDate;
                    addComment(jiraConfig, ticketId, comment);
                }
            }
        }

        if (issueType == IssueType.OPEN){
            if (automatedTestIssue.getFailTimes() == 1 || automatedTestIssue.getCalculatedPriority() == 15){
                boolean response = jiraObjectService.transitionIssue(jiraConfig, ticketId, DashboardNumeration.OPEN.getColumn());
                if (response){
                    log.log(Level.INFO,"Ticket moved to Initial state.");
                    Date executionDate = new Date();
                    String comment = "Ticket set to OPEN state because of failing.";
                    comment += "\n" +"_Date:_ "+executionDate;
                    addComment(jiraConfig, ticketId, comment);
                }
            }
        }
    }

    private void addComment(JiraConfig jiraConfig, String issueID, String newComment){
        jiraObjectService.addJiraComment(jiraConfig, issueID, newComment);
    }


    public ManualTestCase checkManualTestConfig(ManualTestCase manualTestCase, boolean isNew){
        return checkManualTestConfig(manualTestCase, isNew, null, null);
    }

    public ManualTestCase checkManualTestConfig(ManualTestCase manualTestCase, boolean isNew, String prevAutomationStatus, Product prevProduct){
        JiraConfig jiraConfig = null;
        boolean ticketCreated = false;
        String oldTicketId = "";
        boolean closeTicketCreated = false;
        String ticketCreatedID = "";

        if (!isNew){
            //Validates that the Manual test case has a product assigned or it will throw an exception.
            if (manualTestCase.getProduct() != null){
                jiraConfig = jiraConfigService.findByProduct(manualTestCase.getProduct());
            }else{
                //Was null before?
                if (prevProduct != null){
                    //Check if there was a ticket created in Jira and Close it because the Product was removed from the test case.
                    if (manualTestCase.getAutomationExternalId() != null) {
                        ticketCreatedID = manualTestCase.getAutomationExternalId();
                        closeTicketCreated = true;
                        jiraConfig = jiraConfigService.findByProduct(prevProduct);
                    }
                }
            }

            if (jiraConfig != null) {
                if (closeTicketCreated){
                    //Old ticket has to be Closed because Product was deleted.
                    closeManualTestTicket(jiraConfig, ticketCreatedID);
                }else {
                    //Check for existing ticket and set flag to True;
                    if (manualTestCase.getAutomationExternalId() != null) {
                        ticketCreated = true;
                        oldTicketId = manualTestCase.getAutomationExternalId();
                    }

                    //Ticket doesn't exists?, create it.
                    if (!ticketCreated){
                        ticketCreatedID = createManualTestJiraTicket(jiraConfig, manualTestCase);
                        if (!ticketCreatedID.equals("")){
                            manualTestCase.setAutomationExternalId(ticketCreatedID);
                        }
                    }else{
                        //Already has a ticket, check for updates.
                        if (prevProduct == null){
                            //Crear ticket nuevo, con detalles del ticket viejo.
                            ticketCreatedID = createManualTestJiraTicket(jiraConfig, manualTestCase, oldTicketId);
                            if (!ticketCreatedID.equals("")){
                                manualTestCase.setAutomationExternalId(ticketCreatedID);
                            }
                        }else{
                            //updatear ticket.
                            updateManualTestJiraTicket(jiraConfig, manualTestCase, prevAutomationStatus);
                        }
                    }
                }
            }

        }else {
            if (manualTestCase.getProduct() != null) {
                //Product product = productService.findProductById(dto.getProductId());
                jiraConfig = jiraConfigService.findByProduct(manualTestCase.getProduct());
            }
            if (jiraConfig != null) {
                ticketCreatedID = createManualTestJiraTicket(jiraConfig, manualTestCase);
                if (!ticketCreatedID.equals("")){
                    manualTestCase.setAutomationExternalId(ticketCreatedID);
                }
            }
        }


    return manualTestCase;

    }




    public String createManualTestJiraTicket(JiraConfig jiraConfig, ManualTestCase manualTestCase){
       return createManualTestJiraTicket(jiraConfig, manualTestCase, null);
    }

    public String createManualTestJiraTicket(JiraConfig jiraConfig, ManualTestCase manualTestCase, String oldTicketId){
        String LOWER = "5";
        String LOW = "4";
        String MEDIUM = "3";
        String HIGH = "2";
        String TicketId = "";

        String details = buildDetailsBody(manualTestCase);
        if (oldTicketId != null){
            details = "h4. This Manual Test Case was previously filled in another Jira Ticket and closed. Please refer to the ticket ID: "+ oldTicketId + " for more details."+"\n"+ details ;
        }

        switch (manualTestCase.getAutomationStatus().toString()){

            case "PENDING_LOW":
                TicketId = jiraObjectService.createJiraIssue(jiraConfig,"Manual Test: "+manualTestCase.getName(),details, LOW);
                break;

            case "PENDING_MEDIUM":
                TicketId = jiraObjectService.createJiraIssue(jiraConfig,"Manual Test: "+manualTestCase.getName(),details, MEDIUM);
                break;

            case "PENDING_HIGH":
                TicketId = jiraObjectService.createJiraIssue(jiraConfig,"Manual Test: "+manualTestCase.getName(),details, HIGH);
                break;

            case "PENDING_MUST":
                TicketId = jiraObjectService.createJiraIssue(jiraConfig,"Manual Test: "+manualTestCase.getName(),details, LOWER);
                break;

            //No se crea ticket para el resto de los Automations types, El de Difficult significa que es muy dificil de fixear y por eso se cierra o no se crea.
            default:
                break;
        }

        return TicketId;
    }

    private void updateStatus(JiraConfig jiraConfig, AutomationStatusType newStatus, String issueId) {
        switch (newStatus) {

            case PENDING_MUST:
                jiraObjectService.updatePriority(jiraConfig, issueId, "5");
                break;

            case PENDING_LOW:
                jiraObjectService.updatePriority(jiraConfig, issueId, "4");
                break;

            case PENDING_MEDIUM:
                jiraObjectService.updatePriority(jiraConfig, issueId, "3");
                break;

            case PENDING_HIGH:
                jiraObjectService.updatePriority(jiraConfig, issueId, "2");
                break;

        }
    }



    public void updateManualTestJiraTicket(JiraConfig jiraConfig, ManualTestCase manualTestCase, String prevAutomationStatus){
        if (!manualTestCase.getAutomationStatus().toString().equals(prevAutomationStatus)){
            Date executionDate = new Date();
            String comment = "Automation status changed. Priority set from: "+prevAutomationStatus+" to: "+manualTestCase.getAutomationStatus().toString()+"\n" +"Date: "+executionDate;
            String reOpenComment = "Ticket moved to Re Open since it was closed and now set to "+manualTestCase.getAutomationStatus().toString()+"\n" +"Date: "+executionDate;
            String closedComment = "Ticket moved to Done since Automation status was set to "+manualTestCase.getAutomationStatus().toString()+"\n" +"Date: "+executionDate;
            switch (manualTestCase.getAutomationStatus().toString()){

                case "PENDING_LOW":
                    if (prevAutomationStatus.equals("PENDING_MUST") | prevAutomationStatus.equals("PENDING_HIGH") | prevAutomationStatus.equals("PENDING_MEDIUM")){
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), comment);
                    }else {
                        boolean response = jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(), DashboardNumeration.REOPEN.getColumn());
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), reOpenComment);
                    }
                    updateStatus(jiraConfig, manualTestCase.getAutomationStatus(), manualTestCase.getAutomationExternalId());
                    break;

                case "PENDING_MEDIUM":
                    if (prevAutomationStatus.equals("PENDING_MUST") | prevAutomationStatus.equals("PENDING_HIGH") | prevAutomationStatus.equals("PENDING_LOW")){
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), comment);
                    }else {
                        boolean response = jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(), DashboardNumeration.REOPEN.getColumn());
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), reOpenComment);
                    }
                    updateStatus(jiraConfig, manualTestCase.getAutomationStatus(), manualTestCase.getAutomationExternalId());
                    break;

                case "PENDING_HIGH":
                    if (prevAutomationStatus.equals("PENDING_MUST") | prevAutomationStatus.equals("PENDING_LOW") | prevAutomationStatus.equals("PENDING_MEDIUM")){
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), comment);
                    }else {
                        boolean response = jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(),DashboardNumeration.REOPEN.getColumn());
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), reOpenComment);
                    }
                    updateStatus(jiraConfig, manualTestCase.getAutomationStatus(), manualTestCase.getAutomationExternalId());
                    break;

                case "PENDING_MUST":
                    if (prevAutomationStatus.equals("PENDING_LOW") | prevAutomationStatus.equals("PENDING_HIGH") | prevAutomationStatus.equals("PENDING_MEDIUM")){
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), comment);
                    }else {
                        boolean response = jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(), DashboardNumeration.REOPEN.getColumn());
                        addComment(jiraConfig, manualTestCase.getAutomationExternalId(), reOpenComment);
                    }
                    updateStatus(jiraConfig, manualTestCase.getAutomationStatus(), manualTestCase.getAutomationExternalId());
                    break;

                case "DIFFICULT":
                case "NO":
                case "UNDEFINED":
                    jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(), DashboardNumeration.CLOSED.getColumn());
                    addComment(jiraConfig, manualTestCase.getAutomationExternalId(), closedComment);
                    break;

                case "DONE":
                    jiraObjectService.transitionIssue(jiraConfig, manualTestCase.getAutomationExternalId(), DashboardNumeration.RESOLVED.getColumn());
                    addComment(jiraConfig, manualTestCase.getAutomationExternalId(), closedComment);
                    break;
                case "FAILING":
                    break;

            }


            log.log(Level.INFO,"Automation status has changed from: "+prevAutomationStatus +" to: "+manualTestCase.getAutomationStatus().toString());
        }
    }

    public void closeManualTestTicket(JiraConfig jiraConfig, String TicketId){
        Date executionDate = new Date();
        String comment = "Ticket has been closed since the Product was removed from the manual test."+"\n" +"Date: "+executionDate;
        jiraObjectService.transitionIssue(jiraConfig, TicketId, DashboardNumeration.RESOLVED.getColumn());
        addComment(jiraConfig, TicketId, comment);
        log.log(Level.INFO,"Ticket "+ TicketId +" has to be closed");
    }

    private String buildDetailsBody(ManualTestCase manualTestCase){
        String details = "";
        Date executionDate = new Date();
        details += "\n"+"h4. Manual Test Case:" ;
        details += "\n"+"h5. "+manualTestCase.getName() ;
        details += "\n" +"_Updated date:_ "+executionDate;
        if (manualTestCase.getProduct().getName() != null){
            details += "\n" +"_Product:_ "+manualTestCase.getProduct().getName();
        }
        if (manualTestCase.getSuite() != null){
            details += "\n" +"_Suite Type:_ "+manualTestCase.getSuite().toString();
        }

        if (manualTestCase.getRequirement() != null){
            if (manualTestCase.getRequirement().getName() != null){
                details += "\n" +"_Requirements:_ "+manualTestCase.getRequirement().getName();
            }
        }
        details += "\n";
        if (manualTestCase.getLastExecution() != null){
            if (manualTestCase.getLastExecution().getLastExecutionTime() != null){
                Date lastExecutionDate = new Date(manualTestCase.getLastExecution().getLastExecutionTime());
                details += "\n"+"h5. Last Run: "+ lastExecutionDate;
                details += "\n"+"h5. Comment: "+manualTestCase.getLastExecution().getComment() ;
            }
        }


        return details;
    }


}
