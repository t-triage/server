package com.clarolab.service;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.agents.TriageAgent;
import com.clarolab.mapper.impl.NoteMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.*;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.populate.DataProvider;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;

@Component
@Log
@Setter
@Getter
public class ExecutorImportCSVService {
    private boolean autoCreate = false;
    Product productDefault = null;
    Container containerDefault = null;
    ProductComponent componentDefault = null;
    StringBuffer logs;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ConnectorService connectorService;


    public String importReport(String CSVContent, String cvsSplitBy, String format) {
        return importReport(CSVContent, cvsSplitBy, format, null, null);

    }

    public String importReport(String CSVContent, String cvsSplitBy, String format, Container container, Executor executor) {
        setAutoCreate(true);
        initialize();
        Map<TestCase, Note> triageMap = new HashMap<>();
        Build build = null;
        User user = null;

        String lineFeed = "\r\n|\r|\n";
        String[] line;

        String[] lines = CSVContent.split(lineFeed);

        String[] formatList = null;
        if (!Strings.isNullOrEmpty(format)) {
            formatList = format.split(";");
        }

        TestCase testCase;
        TestExecution testExecution;
        Product product = null;
        Note note;

        try {

            if (executor != null) {
                container = executor.getContainer();
                product = executor.getProduct();
            }

            Map<String, List<Integer>> headers = new HashMap<>();

            List<String> headersList = new ArrayList<>(
                    Arrays.asList("Product",
                            "Container Name",
                            "Suite Name",
                            "Test Name",
                            "Test Path",
                            "Triage Notes",
                            "Suite Detail",
                            "Status",
                            "Executed Time",
                            "Severity",
                            "Error Description",
                            "Stack Trace",
                            "Standard Output",
                            "User"));

            if (formatList == null) {
                for (int i = 0; i < headersList.size(); i++){
                    List<Integer> list = new ArrayList<>();
                    list.add(i);
                    headers.put(headersList.get(i), list);
                }
            } else {
                for (int i = 0; i < formatList.length; i++) {
                    List<Integer> list = new ArrayList<>();
                    if (Strings.isNullOrEmpty(formatList[i])){
                        list.add(i);
                    }
                    else {
                        String[] index = formatList[i].split(",");

                        for (int k = 0; k < index.length; k++) {
                            list.add(Integer.parseInt(index[k])-1);
                        }
                    }
                    headers.put(headersList.get(i), list);
                }
            }

            if (lines.length > 1) {
                for (int j = 1; j < lines.length; j++) {
                    testCaseService.cleanNewTests();

                    line = lines[j].split(cvsSplitBy);

                    String productText = headers.containsKey("Product") ?
                            getData(line, headers.get("Product")) : null;

                    String containerText = headers.containsKey("Container Name") ?
                            getData(line, headers.get("Container Name")) : null;

                    String suiteName = headers.containsKey("Suite Name") ?
                            getData(line, headers.get("Suite Name")) : null;

                    String testName = headers.containsKey("Test Name") ?
                            getData(line, headers.get("Test Name")) : null;

                    String testPath = headers.containsKey("Test Path") ?
                            getData(line, headers.get("Test Path")) : null;

                    String triageNotes = headers.containsKey("Triage Notes") ?
                            getData(line, headers.get("Triage Notes")) : null;

                    String executionDetail = headers.containsKey("Execution Detail") ?
                            getData(line, headers.get("Execution Detail")) : null;

                    String executionDate = headers.containsKey("Executed Time") ?
                            getData(line, headers.get("Executed Time")) : null;

                    String statusText = headers.containsKey("Status") ?
                            getData(line, headers.get("Status")) : null;

                    String severityText = headers.containsKey("Severity") ?
                            getData(line, headers.get("Severity")) : null;

                    String errorDescription = headers.containsKey("Error Description") ?
                            getData(line, headers.get("Error Description")) : null;

                    String stackTrace = headers.containsKey("Stack Trace") ?
                            getData(line, headers.get("Stack Trace")) : null;

                    String standardOutput = headers.containsKey("Standard Output") ?
                            getData(line, headers.get("Standard Output")) : null;

                    String userEmail = headers.containsKey("User") ?
                            getData(line, headers.get("User")) : null;

                    user = getUser(user, userEmail);

                    if (build == null) {
                        // This is First Row with Data
                        product = getProduct(product, container, productText);
                        container = getContainer(user, product, container, executor, CSVContent, containerText);
                        executor = getExecutor(container, executor, CSVContent, suiteName);

                        build = DataProvider.getBuild();
                        build.setExecutor(executor);
                        Build lastBuild = buildService.getLastBuild(executor);
                        int buildNumber = 1;
                        if (lastBuild != null) {
                            buildNumber = lastBuild.getNumber() + 1;
                        }
                        build.setNumber(buildNumber);
                        build.setContainer(container);
                        build.setExecutedDate(DateUtils.convertDate(executionDate, DateUtils.BASE_DATE_PATTERN_FORMAT));
                        build.setPopulateMode(PopulateMode.UPLOAD);
                        build.setProcessed(false);
                        build.setDisplayName(String.valueOf(buildNumber));
                        build.setProcessed(false);
                        buildService.setReport(build);

                        build = buildService.save(build);

                        executor.add(build);
                    }
                    testCase = testCaseService.newOrFind(testName, testPath);
                    testCase.setProduct(product);
                    testCase = testCaseService.save(testCase);

                    testExecution = new TestExecution();
                    testExecution.setTestCase(testCase);
                    testExecution.setHasSteps(false);

                    testExecution.setStatus(getStatus(statusText));
                    testExecution.setUserFixPriorityType(getValidPriority(severityText));

                    testExecution.setSuiteName(executionDetail);
                    testExecution.setErrorDetails(errorDescription);
                    testExecution.setErrorStackTrace(stackTrace);
                    testExecution.setStandardOutput(standardOutput);
                    note = getNote(triageNotes, user);
                    if (note != null) {
                        triageMap.put(testCase, note);
                    }

                    build.getReport().add(testExecution);

                    testExecution = testExecutionService.save(testExecution);

                    buildService.setReport(build);

                }
            }
        } catch (Exception e) {
            log(Level.SEVERE, "", e);
        }
        log("Import: To Execute TriageAgent");
        if (executor != null) {
            triageAgent.processExecutor(executor);

            if (build != null) {
                log("Import: To Update Triage Details");

                for (Map.Entry<TestCase, Note> entry : triageMap.entrySet()) {
                    TestTriage tt = testTriageService.findLastTriage(entry.getKey(), build);
                    if (tt != null) {
                        tt.setNote(entry.getValue());
                        tt.setUpdatedByUser(true);
                        if (user != null) {
                            tt.setTriager(user);
                        }
                        testTriageService.update(tt);
                    }
                }
                log("Import: Completed Successfully");
            }
        }
        return logs.toString();
    }

    public String getData(String[] line, List<Integer> indexList){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < indexList.size(); i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(line.length > indexList.get(i) ? line[indexList.get(i)] : "");
        }
        return (result.length() == 0) ? null: result.toString();
    }

    public void initialize() {
        // Maintains a list of notifications performed during the import.
        logs = new StringBuffer();

        // Set a default product if it is possible
        productDefault = null;
        List<Product> products = productService.findAll();
        if (!products.isEmpty()) {
            productDefault = products.get(0);
        }
        containerDefault = containerService.findFirst();
    }

    protected UserFixPriorityType getValidPriority(String priority) {
        UserFixPriorityType defaultValue = UserFixPriorityType.AUTOMATIC;
        if (StringUtils.isEmpty(priority)) {
            return defaultValue;
        }
        priority = priority.toUpperCase();

        List<String> critical = Arrays.asList("CRITICAL", "SEVERE", "HIGH", "P1", "P0", "S1", "S0", "BLOCKER", "MUST");
        List<String> medium = Arrays.asList("IMPORTANT", "MEDIUM", "DEFAULT", "P2", "S2");
        List<String> low = Arrays.asList("USEFUL", "LOW", "P3", "P4", "S3", "S4", "NEGLIGIBLE");

        if (critical.contains(priority)) {
            return UserFixPriorityType.HIGH;
        }
        if (medium.contains(priority)) {
            return UserFixPriorityType.MEDIUM;
        }
        if (low.contains(priority)) {
            return UserFixPriorityType.LOW;
        }

        if (StringUtils.contains(critical, priority)) {
            return UserFixPriorityType.HIGH;
        }
        if (StringUtils.contains(medium, priority)) {
            return UserFixPriorityType.MEDIUM;
        }
        if (StringUtils.contains(low, priority)) {
            return UserFixPriorityType.LOW;
        }

        return defaultValue;
    }

    protected StatusType getStatus(String priority) {
        StatusType defaultValue = StatusType.FAIL;
        if (StringUtils.isEmpty(priority)) {
            return defaultValue;
        }
        priority = priority.toUpperCase();

        List<String> fail = Arrays.asList("FAIL", "CANCEL", "SEVERE", "ERROR", "FALSE", "EXCEPTION");
        List<String> pass = Arrays.asList("PASS", "WORK", "OK", "TRUE", "VALID");
        List<String> skip = Arrays.asList("SKIP", "INVALID");

        if (fail.contains(priority)) {
            return StatusType.FAIL;
        }
        if (pass.contains(priority)) {
            return StatusType.PASS;
        }
        if (skip.contains(priority)) {
            return StatusType.SKIP;
        }

        if (StringUtils.contains(fail, priority)) {
            return StatusType.FAIL;
        }
        if (StringUtils.contains(pass, priority)) {
            return StatusType.PASS;
        }
        if (StringUtils.contains(skip, priority)) {
            return StatusType.SKIP;
        }

        return defaultValue;
    }

    public User getUser(User user, String name) {
        if (user == null && StringUtils.isEmpty(name)) {
            return authContextHelper.getCurrentUser();
        }
        if (StringUtils.isEmpty(name)) {
            return user;
        }
        User userAnswer = userService.findByUsername(name);
        if (userAnswer == null) {
            userAnswer = userService.findByUsername(StringUtils.prepareStringForSearch(name));
        }
        if (userAnswer == null) {
            return user;
        } else {
            return userAnswer;
        }
    }

    public Product getProduct(Product previousProduct, Container container, String productName) {
        if (container != null) {
            return container.getProduct();
        }
        if (StringUtils.isEmpty(productName)) {
            return previousProduct;
        }
        Product product = productService.findProductByName(productName);
        if (product == null) {
            product = productService.findProductByName(StringUtils.prepareStringForSearch(productName));
        }
        if (product == null && !StringUtils.isEmpty(productName) && autoCreate) {
            product = DataProvider.getProduct();
            product.setName(productName.trim());
            product.setDescription("Created at Import");
            product = productService.save(product);
        }
        if (productDefault == null) {
            productDefault = previousProduct;
        }
        if (product == null) {
            product = productDefault;
        }

        return product;
    }

    public Container getContainer(User user, Product product, Container container, Executor executor, String file, String name) {
        if (executor != null) {
            return executor.getContainer();
        }
        if (StringUtils.isEmpty(name)) {
            return container;
        }
        Container containerAnswer = containerService.findByName(product, name);
        if (containerAnswer == null) {
            containerAnswer = containerService.findByName(product, StringUtils.prepareStringForSearch(name));
        }
        if (containerAnswer == null && !StringUtils.isEmpty(name) && autoCreate) {

            Connector connector = getConnector(product, name);
            if (connector == null) {
                connector = DataProvider.getConnector();
                connector.setName(name.trim() + " Import");
                connector.setUrl("Import");
                connector.setType(ConnectorType.UPLOAD);

                connector = connectorService.save(connector);
            }


            containerAnswer = DataProvider.getContainer();
            containerAnswer.setName(name.trim());
            containerAnswer.setDescription("Created at Import");
            containerAnswer.setUrl("Import");
            containerAnswer.setPopulateMode(PopulateMode.UPLOAD);
            containerAnswer.setProduct(product);
            connector.add(containerAnswer);
            containerAnswer = containerService.save(containerAnswer);

            TriageSpec triageSpec = DataProvider.getTriageFlowSpec();
            triageSpec.setContainer(containerAnswer);
            triageSpec.setTriager(user);
            triageSpec = triageSpecService.save(triageSpec);
        }

        if (containerAnswer == null) {
            containerAnswer = container;
        }

        return containerAnswer;
    }

    public Executor getExecutor(Container container, Executor executor, String file, String name) {
        if (StringUtils.isEmpty(name)) {
            return executor;
        }
        Executor executorAnswer = executorService.findExecutorByContainerAndName(container, name);
        if (executorAnswer == null) {
            executorAnswer = executorService.findExecutorByContainerAndName(container, StringUtils.prepareStringForSearch(name));
        }
        if (executorAnswer == null && !StringUtils.isEmpty(name) && autoCreate) {
            executorAnswer = DataProvider.getExecutor();
            executorAnswer.setName(name.trim());
            executorAnswer.setDescription("Created at Import");
            executorAnswer.setContainer(container);
            if (!StringUtils.isEmpty(file)) {
                executorAnswer.setUrl(file.substring(0, 45));
            }
            executorAnswer = executorService.save(executorAnswer);
        }

        return executorAnswer;
    }

    private Connector getConnector(Product product, String name) {
        List<Connector> connectors = null;
        connectors = connectorService.findAll(ConnectorType.UPLOAD);
        if (connectors.isEmpty()) {
            connectors = connectorService.findAll(name);
        }
        if (connectors.isEmpty()) {
            return null;
        }
        return connectors.get(0);
    }

    public Long getTimestamp(Cell dateParam, long defaultValue) {
        if (dateParam == null) {
            return defaultValue;
        } else {
            try {
                Date date = dateParam.getDateCellValue();
                if (date == null) {
                    return defaultValue;
                }
                return date.getTime();
            } catch (Exception ex) {
                try {
                    long timestamp = (long) dateParam.getNumericCellValue();
                    // a timestamp is greater than year 2000
                    if (970757752L < timestamp) {
                        return timestamp;
                    }
                } catch (Exception e) {

                }

            }
        }
        return defaultValue;
    }

    public Note getNote(String noteText, User user) {
        if (StringUtils.isEmpty(noteText)) {
            return null;
        }

        Note note = DataProvider.getNote();
        note.setDescription(noteText);
        note.setAuthor(user);

        note = noteService.save(note);

        return note;

    }

    public void log(Level level, String text, Throwable exception) {
        // keep texts in memory to inform to users
        logs.append(level.getName());
        logs.append(" ");
        logs.append(text);
        logs.append("\n");

        // log in the error log
        if (exception == null) {
            log.log(level, text);
        } else {
            log.log(level, text, exception);
        }
    }

    public void log(Level level, String... texts) {
        StringBuffer logging = new StringBuffer();
        for (String text : texts) {
            logging.append(text);
            logging.append(" ");
        }
        log.log(level, logging.toString());
    }

    public void log(String... texts) {
        log(Level.INFO, texts);
    }

}
