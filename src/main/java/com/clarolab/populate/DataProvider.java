/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.aaa.internal.RegistrationRequest;
import com.clarolab.dto.ProductDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.event.analytics.EvolutionStat;
import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.event.analytics.ProductStat;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.model.*;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.types.*;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.clarolab.util.DateUtils.BaseDateFormat;

public class DataProvider {

    private static final String JENKINS_URL = "http://dev.ttriage.com/view/";

    ;

    public static Report getReport() {
        long timestamp = DateUtils.now();
        return Report.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .description(RandomStringUtils.randomAlphabetic(100))
                .duration(10000L)
                .failCount(RandomUtils.nextInt(0, 500))
                .passCount(RandomUtils.nextInt(0, 1000))
                .skipCount(RandomUtils.nextInt(0, 100))
                .type(ReportType.ROBOT)
                .status(StatusType.PASS)
                .testExecutions(new ArrayList<>())
                .build();
    }

    public static Build getBuild() {
        long timestamp = DateUtils.now();
        return Build.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .number(1)
                .executedDate(timestamp)
                .processed(false)
                .populateMode(PopulateMode.UNDEFINED)
                .buildId("1")
                .displayName(RandomStringUtils.randomAlphabetic(30))
                .status(StatusType.UNKNOWN)
                .report(null)
                .executor(null)
                .build();
    }

    public static TestExecution getTestCase() {
        long timestamp = DateUtils.now();
        TestCase testCase = new TestCase().builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name(RandomStringUtils.randomAlphabetic(30))
                .locationPath(RandomStringUtils.randomAlphabetic(10))
                .build();
        return TestExecution.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .testCase(testCase)
                .suiteName(RandomStringUtils.randomAlphabetic(10))
                .age(10)
                .errorDetails(RandomStringUtils.randomAlphabetic(20))
                .errorStackTrace(RandomStringUtils.randomAlphabetic(100))
                .failedSince(100)
                .duration(10L)
                .status(getRandomStatusType())
                .report(null)
                .build();
    }

    public static StatusType getRandomStatusType() {
        StatusType[] statuses = {
                StatusType.FAIL,
                StatusType.FAIL,
                StatusType.FAIL,
                StatusType.PASS,
                StatusType.SKIP
        };
        return statuses[(int) (Math.random() * (statuses.length - 1))];
    }

    public static Executor getExecutor() {
        long timestamp = DateUtils.now();
        return Executor.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name(RandomStringUtils.randomAlphabetic(30))
                .description(RandomStringUtils.randomAlphabetic(100))
                .url(RandomStringUtils.randomAlphabetic(20))
                .builds(new ArrayList<>())
                .build();
    }


    public static User getUserAsAdmin() {
        return getUserAs(RoleType.ROLE_ADMIN);
    }

    @Deprecated
    public static User getUserAsEditor() {
        return getUserAs(RoleType.ROLE_USER);
    }

    public static User getUserAsViewer() {
        return getUserAs(RoleType.ROLE_USER);
    }

    public static User getUserAs(RoleType role) {
        long timestamp = DateUtils.now();
        return User.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .username(getEmail())
                .realname(RandomStringUtils.randomAlphabetic(30))
                .avatar(ImageModel
                        .builder()
                        .enabled(true)
                        .name("DEMO IMAGE")
                        .timestamp(timestamp)
                        .updated(timestamp)
                        .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                        .build())
                .roleType(role)
                .build();
    }

    public static JiraConfig getJiraConfig(){
        long timestamp = DateUtils.now();
        return JiraConfig.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .build();
    }

    public static Product getProduct() {
        long timestamp = DateUtils.now();
        return Product.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name(getRandomName("Product Provided"))
                .description("DataProvided product")
                .enabled(true)
                .deadlines(new ArrayList<>())
                .build();
    }

    public static ProductDTO getProductDTO() {
        return getProductDTO(getProduct());
    }

    public static ProductDTO getProductDTO(Product product) {
        long timestamp = DateUtils.now();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setEnabled(true);
        productDTO.setTimestamp(timestamp);
        productDTO.setUpdated(timestamp);
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setDeadlines(Lists.newArrayList());
        productDTO.setContainers(Lists.newArrayList());
        productDTO.setNote(null);

        return productDTO;
    }

    public static CVSRepository getCvsRepository() {
        long timestamp = DateUtils.now();
        return CVSRepository.builder()
                .id(1L)
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .enabled(true)
                .build();
    }

    public static Deadline getDeadline() {
        long timestamp = DateUtils.now();
        return Deadline.builder()
                .description("Use this deadline")
                .name(getRandomName("d1.0"))
                .deadlineDate(DataProvider.getTimeAdd(8))
                .timestamp(timestamp)
                .updated(timestamp)
                .enabled(true)
                .build();
    }

    public static Connector getConnector() {
        return Connector.builder()
                .name(DataProvider.getRandomName("Jenkins"))
                .url(JENKINS_URL)
                .type(ConnectorType.JENKINS)
                .userName("testUser")
                .build();
    }

    public static Container getContainer() {
        String name = DataProvider.getRandomName("ContainerProvided");
        return Container.builder()
                .name(name)
                .description("Container suite description for " + name)
                .url(JENKINS_URL + name)
                .populateMode(PopulateMode.PUSH)
                .build();
    }

    public static TriageSpec getTriageFlowSpec() {
        return TriageSpec.builder()
                .expectedMinAmountOfTests(4)
                .frequencyCron(Constants.DEADLINE_FREQUENCY_2DAYS)
                .everyWeeks(2)
                .expectedPassRate(95)
                .priority(2)
                .executor(null)
                .build();
    }

    public static Property getProperty() {
        long timestamp = DateUtils.now();
        return Property.builder()
                .name(getRandomName("data.provider.gen"))
                .description("Generated Property from DataProvider")
                .value("21")
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .build();
    }

    public static IssueTicket getIssueTicket() {
        return IssueTicket.builder()
                .summary(getRandomName("Application section is not working"))
                .description(getRandomName("Description that app section is not working"))
                .url(getRandomName("https://issue.jira.com/TICKET-23"))
                .component("userModule")
                .file("NewFile.pdf")
                .priority(3)
                .dueDate(getTimeAdd(8))
                .issueType(IssueType.OPEN)
                .build();
    }

    public static Note getNote() {
        long timestamp = DateUtils.now();
        return Note.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name(getRandomName("Note Provided"))
                .description("DataProvided Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                .build();
    }

    public static ApplicationEvent getApplicationEvent() {
        long timestamp = DateUtils.now();
        return ApplicationEvent.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .eventTime(timestamp)
                .processed(false)
                .displayName(getRandomName("Event Provided"))
                .type(ApplicationEventType.TRIAGE_AGENT_EXECUTED)
                .originatingClass("Product")
                .originatingMethod("provider")
                .build();
    }

    public static SlackSpec getSlackSpec() {
        return SlackSpec.builder()
                .channel("testpopulate")
                .token("xoxp-546678226724-546101465248-548197344391-132f5c06df0be7ba24c435f35c412a3e")
                .build();
    }

    public static AutomatedTestIssue getAutomatedTestIssue() {
        long timestamp = DateUtils.now();
        return AutomatedTestIssue.builder()
                .enabled(true)
                .timestamp(timestamp)
                .issueType(getRandomIssueType())
                .build();
    }

    public static ApplicationDomain getApplicationDomain() {
        long timestamp = DateUtils.now();
        return ApplicationDomain.builder()
                .enabled(true)
                .timestamp(timestamp)
                .domainName(getRandomName("", 2) + "domain.com")
                .allowed(true)
                .build();
    }

    public static Artifact getArtifact() {
        long timestamp = DateUtils.now();
        return Artifact.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name("test")
                .url("http://test.com/url")
                .artifactType(ArtifactType.LOG)
                .build();
    }

    public static IssueType getRandomIssueType() {
        IssueType[] statuses = {
                IssueType.OPEN,
                IssueType.FIXED,
                IssueType.REOPEN
        };
        return statuses[(int) (Math.random() * (statuses.length - 1))];
    }

    public static TestPin getPin() {
        return TestPin.builder()
                .createDate(DateUtils.now())
                .reason("Test")
                .build();
    }

    public static ExecutorStat getExecutorStat() {
        long timestamp = DateUtils.now();
        ExecutorStat stat = ExecutorStat.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .actualDate(BaseDateFormat.format(new Date()))
                .state(StateType.PASS)
                .tags(getRandomName("tags"))
                .pass(getRandomNumber(0,2))
                .skip(getRandomNumber(0,2))
                .newFails(getRandomNumber(0,2))
                .fails(getRandomNumber(0,2))
                .nowPassing(getRandomNumber(0,2))
                .toTriage(getRandomNumber(0,2))
                .duration(getRandomNumber(0,2))
                .stabilityIndex(getRandomNumber(0,2))
                .executionDate(BaseDateFormat.format(new Date()))
                .assignee(getRandomName("User Name"))
                .priority(2)
                .productName(getRandomName("Product"))
                .suiteName(getRandomName("Suite"))
                .containerName(getRandomName("Container"))
                .defaultPriority(2)
                .deadline(BaseDateFormat.format(getTimeAdd((int) getRandomNumber(0,1))))
                .daysToDeadline((int) getRandomNumber(0,1))
                .evolutionPass((int) getRandomNumber(0,1))
                .evolutionSkip((int) getRandomNumber(0,1))
                .evolutionNewFails((int) getRandomNumber(0,1))
                .evolutionFails((int) getRandomNumber(0,1))
                .evolutionNowPassing((int) getRandomNumber(0,1))
                .evolutionToTriage((int) getRandomNumber(0,1))
                .build();
        stat.setTotalTests(stat.getFails() + stat.getPass() + stat.getSkip() + stat.getNewFails() + stat.getNowPassing());
        stat.setMaxExecutedTest(stat.getTotalTests() + (int) getRandomNumber(0,1));

        return stat;
    }

    public static EvolutionStat getEvolutionStat() {
        long timestamp = DateUtils.now();
        EvolutionStat evolutionStat = EvolutionStat.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .executionDate(timestamp)
                .growth((int) getRandomNumber(0,1))
                .passing((int) getRandomNumber(0,1))
                .commits((int) getRandomNumber(0,1))
                .triageDone((int) getRandomNumber(0,1))
                .stability((int) getRandomNumber(0,1))
                .build();

        return evolutionStat;
    }

    public static Functionality getFunctionality() {
        long timestamp = DateUtils.now();
        Functionality functionality = Functionality.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .name(getRandomName("functionality", 5))
                .risk("Risk")
                .story("Story")
                .externalId("20")
                .build();

        return functionality;
    }

    public static ProductStat getProductStat() {
        long timestamp = DateUtils.now();
        ProductStat stat = ProductStat.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .actualDate(BaseDateFormat.format(new Date()))
                .product(getProduct())
                .pass(getRandomNumber(0,2))
                .skip(getRandomNumber(0,2))
                .newFails(getRandomNumber(0,2))
                .fails(getRandomNumber(0,2))
                .nowPassing(getRandomNumber(0,2))
                .toTriage(getRandomNumber(0,2))
                .autoTriaged(getRandomNumber(0,2))
                .commits ((int) getRandomNumber(0,2))
                .repositoryLinesChanged((int) getRandomNumber(0,2))
                .duration(getRandomNumber(0,2))
                .stabilityIndex(getRandomNumber(0,2))
                .executionDate(BaseDateFormat.format(new Date()))
                .deadline(BaseDateFormat.format(getTimeAdd((int) getRandomNumber(0,1))))
                .daysToDeadline((int) getRandomNumber(0,1))
                .deadlinePriority(2)
                .evolutionPass((int) getRandomNumber(0,1))
                .evolutionSkip((int) getRandomNumber(0,1))
                .evolutionNewFails((int) getRandomNumber(0,1))
                .evolutionFails((int) getRandomNumber(0,1))
                .evolutionNowPassing((int) getRandomNumber(0,1))
                .evolutionToTriage((int) getRandomNumber(0,1))
                .build();
        stat.setTotalTests(stat.getFails() + stat.getPass() + stat.getSkip() + stat.getNewFails() + stat.getNowPassing());

        return stat;
    }

    public static String getEmail() {
        return getEmail(null);
    }

    public static String getEmail(String base) {
        if (base == null) {
            return RandomStringUtils.randomAlphabetic(10) + "@ttriage.com";
        } else {
            String userSanitized = base.replace(" ", "");
            return userSanitized + RandomStringUtils.randomAlphabetic(2) + "@ttriage.com";
        }
    }

    public static String getRandomName(String base) {
        if (base == null || base.length() <= 10) {
            return getRandomName(base, 4);
        } else {
            return getRandomName(base, 2);
        }

    }

    public static String getRandomName(String base, int amount) {
        if (base == null) {
            return RandomStringUtils.randomAlphabetic(amount);
        } else {
            return base + RandomStringUtils.randomAlphabetic(amount);
        }
    }

    public static long getRandomNumber(int base, int digits) {
        StringBuffer numericString = new StringBuffer();
        if (base != 0) {
            numericString.append(base);
        }
        for (int i = 0; i < digits ; i++) {
            numericString.append(((int) (Math.random() * 9) + 1));
        }
        return Long.parseLong(numericString.toString());

    }

    public static String getRandomHash() {
        return Integer.toHexString(RandomUtils.nextInt());
    }

    public static long getTimeAdd(int days) {
        return getTimeAdd(new Date().getTime(), days);
    }

    public static long getTimeAdd(long fromTime, int days) {
        Timestamp timestamp = new Timestamp(fromTime);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_MONTH, days);
        timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp.getTime();
    }

    public static long getTime() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return timestamp.getTime();
    }

    public static RegistrationRequest getRegistration() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(getEmail());
        registrationRequest.setName(RandomStringUtils.randomAlphabetic(10));
        registrationRequest.setPassword(RandomStringUtils.randomAlphabetic(10));
        return registrationRequest;
    }

    public static UserPreferenceDTO getNewPreference(UserDTO userDTO) {
        UserPreferenceDTO userPreferenceDTO = new UserPreferenceDTO();
        userPreferenceDTO.setEnabled(true);
        userPreferenceDTO.setCurrentContainer(1);
        userPreferenceDTO.setCurrentPageNUmber(1);
        userPreferenceDTO.setRowPerPage(1);
        userPreferenceDTO.setTimestamp(getTimeAdd(5));
        userPreferenceDTO.setUpdated(new Long("0"));
        userPreferenceDTO.setUser(userDTO);
        return userPreferenceDTO;
    }

    public static ExternalBuildTriage getExternalBuildTriage() {
        long timestamp = DateUtils.now();
        ExternalBuildTriage build = ExternalBuildTriage.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .suiteId(getRandomName(null, 3))
                .suiteName(getRandomName(null, 3))
                .priority(1)
                .buildNumber(4)
                .executedTime(timestamp)
                .productBuildVersion("myProduct 4.3.12")
                .triageDeadline(DateUtils.beginDay(5))
                .triaged(false)
                .triager(null)
                .note(null)
                .totalNewFails(getRandomNumber(3, 1))
                .totalNewPass(getRandomNumber(5, 1))
                .totalNotExecuted(getRandomNumber(3, 2))
                .totalTriageDone(getRandomNumber(0, 1))
                .totalFails(getRandomNumber(14, 2))
                .totalPass(getRandomNumber(30, 3))
                .build();
        build.setTotalTests();

        return build;
    }
}
