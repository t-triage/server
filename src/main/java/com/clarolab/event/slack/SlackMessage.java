package com.clarolab.event.slack;

import com.clarolab.event.analytics.EvolutionProductStat;
import com.clarolab.event.analytics.EvolutionStat;
import com.clarolab.model.*;
import com.clarolab.service.ApplicationDomainService;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.ExecutorView;
import com.clarolab.view.GroupedStatView;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.clarolab.util.Constants.*;

@Log
@Component
public class SlackMessage {

    @Autowired
    private ApplicationDomainService applicationDomainService;

    boolean includeAttachmentParam = false;

    static final String newFailColor = ", {\"color\": \"#cb4335\", "; //Rojo

    static final String failColor = ", {\"color\": \"#cb4335\", "; // Rojo

    static final String nowPassingColor = ", {\"color\": \"#6dad06\", "; // Verde discreto

    static final String triagedColor = ", {\"color\": \"#F0FF0F\", "; //Green fluor



    public String getRawStringMessage(ExecutorView view) {

        // Builds a message like: personName @ ContainerName: Tests: 140 | New Fails to triage: 15 | Fails to triage: 20 | Now Passing to triage: 3 | Done: 22.

        StringBuffer message = new StringBuffer();
        message.append(view.getAssignee().getRealname());
        message.append(" @ ");
        message.append(view.getContainerName());
        message.append(" > ");
        if (view.getTotalTests() > 0) {
            message.append("Tests: ");
            message.append(view.getTotalTests());

            if (view.getTotalNewFails() > 0) {
                message.append(" | New Fails TT: ");
                message.append(view.getTotalNewFails());
            }

            if (view.getTotalFails() > 0) {
                message.append(" | Fails TT: ");
                message.append(view.getTotalFails());
            }

            if (view.getTotalNowPassing() > 0) {
                message.append(" | Now Passing TT: ");
                message.append(view.getTotalNowPassing());
            }
            if (view.getTotalTriageDone() > 0) {
                message.append(" | Triaged: ");
                message.append(view.getTotalTriageDone());
            }
            if (view.getToTriage() == 0) {
                message.append(" | All Triaged. Nothing to do");
                return null;
            } else {
                message.append(" | Total Pending TT: ");
                message.append(view.getToTriage());
            }
        } else {
            message.append(" without Tests: ");
            return null;
        }

        return message.toString();
    }


    /**
     * Builds something nice like :
     * <p>
     * Pending to Triage: 7 / 430
     * Assingee User
     * JRF-develop-Activity-Email-Chrome
     * New Fails: 2
     * Fails: 5
     * Triaged: 43
     * <p>
     * Slack doc: https://api.slack.com/docs/message-attachments
     *
     * @param view
     * @return
     */

    public String getSuiteMessageAttachment(String mainTitle, ExecutorView view) {



        if (view.getTotalTests() != 0 && view.getToTriage() == 0) {
            // There isn't anything pending to triage. We wont spam.
            return null;
        }

        String notificationBox = "";
        String pendingToTriage = "";
        String userMention = getUserMention(view.getTriageSpec().getTriager());

        int failsToTriage = (int) view.getToTriage();

        if (view.getTotalTests() == 0) {
            notificationBox = "t-Triage: Build's Failed";
            pendingToTriage = mainTitle + ": The last build has failed and could not execute any test.";
        } else {
            notificationBox = "t-Triage: " + failsToTriage + " / " + view.getTotalTests();
            pendingToTriage = mainTitle + ": " + failsToTriage + " / " + view.getTotalTests();
        }
        String userName = view.getAssignee().getRealname();
        String userLink = StringUtils.concatURL(applicationDomainService.getURL(), String.format(URL_FRONT_USER, view.getAssignee().getId()));

        String suiteName = view.getExecutorName() + " #" + view.getBuildNumber();
        String suiteLink = StringUtils.concatURL(applicationDomainService.getURL(), String.format(URL_FRONT_EXECUTOR, view.getExecutorId()));

        String newFails = "";
        if (view.getTotalNewFails() > 0) {
            newFails = "New Fails: " + view.getTotalNewFails();
        }

        String fails = "";
        if (view.getTotalFails() > 0) {
            fails = "Fails: " + view.getTotalFails();
        }

        String nowPassing = "";
        if (view.getTotalNowPassing() > 0) {
            nowPassing = "Now Passing: " + view.getTotalNowPassing();
        }

        String triaged = "";
        if (view.getTotalTriageDone() > 0) {
            triaged = "Triaged: " + view.getTotalTriageDone();
        }

        StringBuffer message = new StringBuffer();

        // header begin
        if (includeAttachmentParam) {
            message.append("{\"attachments\": ");
        }

        // notification box
        message.append("[{\"fallback\": \"");
        message.append(notificationBox);
        message.append("\", ");

        // "pretext": " Pending to Triage: 7 / 430",
        message.append("\"pretext\": \"");
        message.append(pendingToTriage);
        message.append("\", ");

        // "author_name": "Assingee User",
        message.append("\"author_name\": \"");
        if (StringUtils.isEmpty(userMention)) {
            message.append(userName);
        } else {
            message.append(userMention);
        }
        message.append("\", ");

        /**
         // "author_link": "http://localhost/user/333",
         message.append("\"author_link\": \"");
         message.append(userLink);
         message.append("\", ");
         **/

        // "title": "JRF-develop-Activity-Email-Chrome",
        message.append("\"title\": \"");
        message.append(suiteName);
        message.append("\", ");

        // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban"
        message.append("\"title_link\": \"");
        message.append(suiteLink);
        message.append("\"");

        // header end
        message.append("}");

        // NEW FAILS SECTION
        if (!newFails.isEmpty()) {
            //message.append(", {\"color\": \"#B22222\", ");
            message.append(newFailColor);
            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(suiteLink);
            message.append("\", ");

            // "text": "New Fails: 2"
            message.append("\"text\": \"");
            message.append(newFails);
            message.append("\"}");
        }

        // FAILS SECTION
        if (!fails.isEmpty()) {
            //message.append(", {\"color\": \"#FF0000\", ");
            message.append(failColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(suiteLink);
            message.append("\", ");

            // "text": "Fails: 2"
            message.append("\"text\": \"");
            message.append(fails);
            message.append("\"}");
        }

        // TRIAGED SECTION
        if (!nowPassing.isEmpty()) {
            //message.append(", {\"color\": \"#C7F312\", ");
            message.append(nowPassingColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(suiteLink);
            message.append("\", ");

            // "text": "NOW PASSING: 2"
            message.append("\"text\": \"");
            message.append(nowPassing);
            message.append("\"}");
        }

        // TRIAGED SECTION
        if (!triaged.isEmpty()) {
            //message.append(", {\"color\": \"#2EB886\", ");
            message.append(triagedColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(suiteLink);
            message.append("\", ");

            // "text": "Triaged: 2"
            message.append("\"text\": \"");
            message.append(triaged);
            message.append("\"} ");
        }

        message.append("]");

        if (includeAttachmentParam) {
            message.append("}");
        }

        return message.toString();
    }


    /**
     * Builds something nice like :
     * <p>
     * Pending to Triage: 7 / 430
     * Content Regression Tests
     * New Fails: 2
     * Fails: 5
     * Triaged: 43
     * <p>
     * Slack doc: https://api.slack.com/docs/message-attachments
     *
     * @param view
     * @return
     */

    public String getContainerMessageAttachment(String mainTitle, GroupedStatView view, Container container) {

        if (view.getTotal() != 0 && view.getToTriage() == 0) {
            // There isn't anything pending to triage. We wont spam.
            return null;
        }

        String notificationBox = "";
        String pendingToTriage = "";

        int failsToTriage = (int) view.getToTriage();

        if (view.getTotal() == 0) {
            notificationBox = "t-Triage: Build's Failed";
            pendingToTriage = mainTitle + ": There aren't any test in it.";
        } else {
            String userMention = getUserMention(view.getAssignee());
            if (StringUtils.isEmpty(userMention)) {
                userMention = "";
            } else {
                userMention = " " + userMention;
            }
            notificationBox = mainTitle + ": " + failsToTriage + " / " + view.getTotal() + userMention;
            pendingToTriage = notificationBox;
        }
        String name = view.getName();
        String link = StringUtils.concatURL(applicationDomainService.getURL(), String.format(URL_FRONT_CONTAINER, container.getId()));

        String newFails = "";
        if (view.getNewFails() > 0) {
            newFails = "New Fails: " + view.getNewFails();
        }

        String fails = "";
        if (view.getFails() > 0) {
            fails = "Fails: " + view.getFails();
        }

        String nowPassing = "";
        if (view.getNowPassing() > 0) {
            nowPassing = "Now Passing: " + view.getNowPassing();
        }

        String triaged = "";
        if (view.getTriaged() > 0) {
            triaged = "Triaged: " + view.getTriaged();
        }

        StringBuffer message = new StringBuffer();

        // header begin
        if (includeAttachmentParam) {
            message.append("{\"attachments\": ");
        }

        // notification box
        message.append("[{\"fallback\": \"");
        message.append(notificationBox);
        message.append("\", ");

        // "pretext": " Pending to Triage: 7 / 430",
        message.append("\"pretext\": \"");
        message.append(pendingToTriage);
        message.append("\", ");


        // "title": "JRF-develop-Activity-Email-Chrome",
        message.append("\"title\": \"");
        message.append(name);
        message.append("\", ");

        // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban"
        message.append("\"title_link\": \"");
        message.append(link);
        message.append("\"");

        // header end
        message.append("}");

        // NEW FAILS SECTION
        if (!newFails.isEmpty()) {
            //message.append(", {\"color\": \"#B22222\", ");
            message.append(newFailColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(link);
            message.append("\", ");

            // "text": "New Fails: 2"
            message.append("\"text\": \"");
            message.append(newFails);
            message.append("\"}");
        }

        // FAILS SECTION
        if (!fails.isEmpty()) {
            //message.append(", {\"color\": \"#FF0000\", ");
            message.append(failColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(link);
            message.append("\", ");

            // "text": "Fails: 2"
            message.append("\"text\": \"");
            message.append(fails);
            message.append("\"}");
        }

        // TRIAGED SECTION
        if (!nowPassing.isEmpty()) {
            //message.append(", {\"color\": \"#C7F312\", ");
            message.append(nowPassingColor);

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(link);
            message.append("\", ");

            // "text": "NOW PASSING: 2"
            message.append("\"text\": \"");
            message.append(nowPassing);
            message.append("\"}");
        }

        // TRIAGED SECTION
        if (!triaged.isEmpty()) {
            //message.append(", {\"color\": \"#2EB886\", ");
            message.append(triagedColor);
            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(link);
            message.append("\", ");

            // "text": "Triaged: 2"
            message.append("\"text\": \"");
            message.append(triaged);
            message.append("\"} ");
        }

        message.append("]");

        if (includeAttachmentParam) {
            message.append("}");
        }

        return message.toString();
    }

    private String getUserMention(User user) {
        if (user == null || StringUtils.isEmpty(user.getSlackId())) {
            return null;
        }
        return "<@" + user.getSlackId() + ">";
    }

    public String sendAutomationPending(SlackSpec slackSpec, long automationPending, long automationPendingThisPeriod, long manualTestPending, long manualTestPendingThisPeriod) {
        String fixLink = StringUtils.concatURL(applicationDomainService.getURL(), "/AutomationIssues");
        String createLink = StringUtils.concatURL(applicationDomainService.getURL(), "/AutomationCreation");

        String notificationBox = "";
        StringBuffer message = new StringBuffer();

        // header begin
        if (includeAttachmentParam) {
            message.append("{\"attachments\": ");
        }

        // notification box
        message.append("[{\"fallback\": \"");
        message.append(notificationBox);
        message.append("\", ");

        // "pretext": " Pending to Triage: 7 / 430",
        message.append("\"pretext\": \"");
        message.append("Tests requiring coding for: ");
        message.append(slackSpec.getContainerName());
        message.append("\" ");

        // header end
        message.append("}");

        if (automationPending > 0) {
            message.append(", {\"color\": \"#2196f3\", ");

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(fixLink);
            message.append("\", ");

            // "text": "New Fails: 2"
            message.append("\"title\": \"");
            message.append("t Fix: ");
            message.append(automationPending);

            if (automationPendingThisPeriod > 0) {
                message.append(" (+");
                message.append(automationPendingThisPeriod);
                message.append(")");
            }

            message.append("\"}");
        }

        if (manualTestPending > 0) {
            message.append(", {\"color\": \"#4dab56\", ");

            // "title_link": "http://tdev.clarolab.com/Dashboard/500008/Kanban",
            message.append("\"title_link\": \"");
            message.append(createLink);
            message.append("\", ");

            // "text": "New Fails: 2"
            message.append("\"title\": \"");
            message.append("t Create: ");
            message.append(manualTestPending);

            if (manualTestPendingThisPeriod > 0) {
                message.append(" (+");
                message.append(manualTestPendingThisPeriod);
                message.append(")");
            }

            message.append("\"}");
        }

        message.append("]");

        if (includeAttachmentParam) {
            message.append("}");
        }

        return message.toString();
    }

    public String getDailyExecutor(String mainTitle, ExecutorView view) {

        StringBuffer message = new StringBuffer();
        message.append("{");

        // "pretext": " Pending to Triage: 7 / 430",
        message.append("\"pretext\": \"");
        if (!StringUtils.isEmpty(mainTitle)) {
            message.append(mainTitle);
            message.append("\\n");
        }
        message.append(view.getExecutorName());
        message.append(" #");
        message.append(view.getBuildNumber());
        if (view.getTotalTests() > 0) {
            message.append(" (");
            message.append(view.getTotalTests());
            message.append(" tests)");
        }

        message.append("\", ");
        
        String color = null;
        String text = null;
        if (view.getTotalTests() == 0) {
            // Job without tests, probably have failed.
            text = "The last build has failed and could not execute any test.";
            color = "CB4335";
        } else if (view.getToTriage() == 0 && view.getTotalTriageDone() == 0) {
            // All Pass
            text = "All tests are passing";
            color = "6dad06";
        } else if (view.getToTriage() == 0 && view.getTotalTriageDone() > 0) {
            // All Triaged
            text = "All tests are triaged.";
            color = "00FF0F";
        }
        
        if (!StringUtils.isEmpty(text)) {
            // printing recorded text
            
            message.append("\"color\": \"#");
            message.append(color);
            message.append("\", ");
            
            message.append("\"text\": \"");
            message.append(text);
            message.append("\" ");
            
            message.append("}");

            return message.toString();
        }

        message.append("\"ts\": \"");
        message.append(view.getBuildTriage().getDeadline());
        message.append("\", ");

        // FAILS
        message.append("\"color\": \"#");
        message.append("CB4335");
        message.append("\", ");

        message.append("\"text\": \"");
        message.append("Fails: ");
        message.append(view.getToTriage());
        message.append("\" ");

        message.append("}, ");


        // TO TRIAGE
        message.append("{");
        message.append("\"color\": \"#");
        message.append("00FF0F");
        message.append("\", ");

        message.append("\"text\": \"");
        message.append("Triaged: ");
        message.append(view.getTotalTriageDone());
        message.append("\" ");

        message.append("} ");


        return message.toString();
    }
    
    public String getAttachmentHeader(String pretext) {
        StringBuffer message = new StringBuffer();

        // header begin
        if (includeAttachmentParam) {
            message.append("{\"attachments\": ");
        }
        
        message.append("[");
        
        if (!StringUtils.isEmpty(pretext)) {
            message.append("{");
            message.append("\"pretext\": \"");
            message.append(pretext);
            message.append("\" ");

            message.append("} ");
        }
        
        return message.toString();
    }

    public String getAttachmentFooter(String footer) {
        StringBuffer message = new StringBuffer();

        if (!StringUtils.isEmpty(footer)) {
            message.append(", {");
            message.append("\"pretext\": \"");
            message.append(footer);
            message.append("\" ");

            message.append("} ");
        }
        
        message.append("]");
        
        if (includeAttachmentParam) {
            message.append("}");
        }

        return message.toString();
    }

    public String getWeeklyExecutor(String mainTitle, Executor executor, EvolutionStat stat) {

        StringBuffer message = new StringBuffer();
        message.append(", {");

        message.append("\"pretext\": \"");
        if (!StringUtils.isEmpty(mainTitle)) {
            message.append(mainTitle);
            message.append("\\n");
        }
        message.append("*");
        message.append(executor.getName());
        message.append("*");

        //El "stat == null" lo dejo por las dudas que tenga algún error.
        //Igualmente si no tiene seteado algun Goal directamente le pone la leyenda "no activity"
        if (stat == null || executor.getGoal() == null) {
            message.append(" - no goal set -");
        } else {
            message.append(" (");
            message.append(stat.getTotalTests());
            message.append(" tests)");
        }
        message.append("\", ");


        if (stat == null || executor.getGoal() == null) {
            Build lastBuild = executor.getLastExecutedBuild();
            if (lastBuild != null || executor.getGoal() == null) {
                message.append("\"ts\": \"");
                message.append(lastBuild.getExecutedDate());
                message.append("\" ");
            }

            message.append("} ");
            return message.toString();
        }

        message.append("\"fields\": [ {");

        message.append("\"title\": \"Index\",\n\n");
       // message.append("\"value\": \"\n\nGrowth: \n\nTriage Done:\n\nPassing: \n\nStability: \",\n");
        message.append("\"value\":");
        message.append("\"");
        if(executor.getGoal().getRequiredGrowth() != null && executor.getGoal().getExpectedGrowth() != null){
            message.append("\n\nGrowth:");
        }
        if(executor.getGoal().getRequiredTriageDone() != null && executor.getGoal().getExpectedTriageDone() != null){
            message.append("\n\nTriage Done:");
        }
        if(executor.getGoal().getRequiredPassing() != null && executor.getGoal().getExpectedPassing() != null){
            message.append("\n\nPassing");
        }
        if(executor.getGoal().getRequiredStability() != null && executor.getGoal().getExpectedStability() != null){
            message.append("\n\nStability: ");
        }
        if(executor.getGoal().getRequiredCommits() != null && executor.getGoal().getExpectedCommits() != null){
            message.append("\n\nCommits: ");
        }
        message.append("\",\n");

        message.append("\"short\": true\n");

        message.append("},{");

        message.append(" \"title\": \"Goal\",\n");

        String icons = DateUtils.now() % 2 == 0 ?
                "                    \"value\": \":arrow_heading_up:\n:arrow_up:\n:keycap_ten:\n:top:\",\n"
                :
                "                    \"value\": \":small_blue_diamond:\n:zero:\n:arrow_down:\n:arrows_clockwise:\",\n";
        Map<Integer, String> starIcons = new HashMap<>(3);
        starIcons.put(-1, ":-1:");
        // starIcons.put(0, ":black_small_square:");
        // starIcons.put(0, ":small_blue_diamond:");
        // starIcons.put(0, ":zero:");
        // Others: warning arrows_clockwise zero ok small_blue_diamond zero
        starIcons.put(0, ":+1:");
        starIcons.put(1, ":clap:");




        message.append(" \"value\": \"");
        if(executor.getGoal().getRequiredGrowth() != null && executor.getGoal().getExpectedGrowth() != null) {
            message.append(starIcons.getOrDefault(stat.getGrowth(), "-"));
            message.append("\n");
        }
        if(executor.getGoal().getRequiredTriageDone() != null && executor.getGoal().getExpectedTriageDone() != null) {
            message.append(starIcons.getOrDefault(stat.getTriageDone(), "-"));
            message.append("\n");
        }
        if(executor.getGoal().getRequiredPassing() != null && executor.getGoal().getExpectedPassing() != null) {
            message.append(starIcons.getOrDefault(stat.getPassing(), "-"));
            message.append("\n");
        }
        if(executor.getGoal().getRequiredStability() != null && executor.getGoal().getExpectedStability() != null) {
            message.append(starIcons.getOrDefault(stat.getStability(), "-"));
            message.append("\n");
        }
        if(executor.getGoal().getRequiredCommits() != null && executor.getGoal().getExpectedCommits() != null) {
            message.append(starIcons.getOrDefault(stat.getCommits(), "-"));
            message.append("\n");
        }

        message.append("\",\n");

        message.append(" \"short\": true\n");

        message.append(" }]");

        message.append("}, ");


        // Agile Index
        message.append("{");
        message.append("\"color\": \"#");
        if (stat.getStabilityIndex() < 2) {
            message.append("FF0000");
        } else if (stat.getStabilityIndex() < 3) {
            message.append("C7F312");
        } else if (stat.getStabilityIndex() <= 6) {
            message.append("2EB886");
        }
        message.append("\", ");

        message.append("\"text\": \"");
        message.append("Agile Index: ");
        message.append(starIcons.getOrDefault(stat.getStabilityIndex(), "-"));
        message.append("\", ");

        message.append("\"ts\": \"");
        message.append(stat.getExecutionDate());
        message.append("\", ");

        message.append("} ");


        return message.toString();
    }


    public String getWeeklyProduct(String mainTitle, Product product, EvolutionProductStat stat) {

        StringBuffer message = new StringBuffer();
        message.append(", {");

        message.append("\"pretext\": \"");
        if (!StringUtils.isEmpty(mainTitle)) {
            message.append(mainTitle);
            message.append("\\n");
        }
        message.append("*");
        message.append(product.getName());
        message.append("*");

        //El "stat == null" lo dejo por las dudas que tenga algún error.
        //Igualmente si no tiene seteado algun Goal directamente le pone la leyenda "no activity"
        if (stat == null || product.getGoal() == null) {
            message.append(" - no goal set -");

        } else {
            message.append(" (");
            message.append(stat.getTotalTests());
            message.append(" tests)");
        }
        message.append("\", ");




        if (stat != null || product.getGoal() != null) {
            message.append("\"fields\": [ {");

            message.append("\"title\": \"Index\",\n\n");
            message.append("\"value\":");
            message.append("\"");

            if (product.getGoal().getRequiredTestCase() != null && product.getGoal().getExpectedTestCase() != null && stat != null) {
                message.append("\n\nTest Case:");
            }
            if (product.getGoal().getRequiredPassRate() != null && product.getGoal().getExpectedPassRate() != null && stat != null) {
                message.append("\n\nPass Rate:");
            }

            message.append("\",\n");

            message.append("\"short\": true\n");

            message.append("},{");

            message.append(" \"title\": \"Goal\",\n");

            String icons = DateUtils.now() % 2 == 0 ?
                    "                    \"value\": \":arrow_heading_up:\n:arrow_up:\n:keycap_ten:\n:top:\",\n"
                    :
                    "                    \"value\": \":small_blue_diamond:\n:zero:\n:arrow_down:\n:arrows_clockwise:\",\n";
            Map<Integer, String> starIcons = new HashMap<>(3);
            starIcons.put(-1, ":-1:");
            starIcons.put(0, ":+1:");
            starIcons.put(1, ":clap:");


            message.append(" \"value\": \"");
            if (product.getGoal().getRequiredTestCase() != null && product.getGoal().getExpectedTestCase() != null && stat != null) {
                message.append(starIcons.getOrDefault(stat.getTotalTests(), "-"));
                message.append("\n");
            }
            if (product.getGoal().getRequiredPassRate() != null && product.getGoal().getExpectedPassRate() != null && stat != null) {
                message.append(starIcons.getOrDefault(stat.getPassRate(), "-"));
                message.append("\n");
            }

            message.append("\",\n");

            message.append(" \"short\": true\n");

            message.append(" }]");


        }
        message.append("} ");
        return message.toString();
    }

    public String getWeeklyEmptyContainer(Container entity, SlackSpec lackSpec) {
        StringBuffer message = new StringBuffer();


        // No value
        message.append(", {");
        message.append("\"color\": \"#");
        message.append("505050");
        message.append("\", ");

        message.append("\"text\": \"");
        message.append("Test suites weren't executed/analyzed.");
        message.append("\", ");

        message.append("} ");

        return message.toString();
    }

    public String getWeeklyEmptyProduct(SlackSpec lackSpec) {
        StringBuffer message = new StringBuffer();


        // No value
        message.append(", {");
        message.append("\"color\": \"#");
        message.append("505050");
        message.append("\", ");

        message.append("\"text\": \"");
        message.append("Test suites weren't executed/analyzed.");
        message.append("\", ");

        message.append("} ");

        return message.toString();
    }
    
}
