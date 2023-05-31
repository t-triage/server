package com.clarolab.service;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.agents.TriageAgent;
import com.clarolab.jira.service.JiraAutomationService;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

@Component
@Log
@Setter
@Getter
public class ExecutorImportService {
    private boolean autoCreate = false;
    Product productDefault = null;
    Container containerDefault = null;
    ProductComponent componentDefault = null;
    StringBuffer logs;

    @Autowired
    private JiraAutomationService jiraAutomationService;

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


    final int posProduct = 0;
    final int posContainer = 1;
    final int posSuiteName = 2;
    final int posTestName = 3;
    final int posTestPath = 4;
    final int posTriageNotes = 5;
    final int posExecutionDetail = 6;
    final int posStatus = 7;
    final int posExecutedTime = 8;
    final int posSeverity = 9;
    final int posErrorDescription = 10;
    final int posStackTrace = 11;
    final int posStandardOutput = 12;
    final int posUser = 13;

    public String importReport(String file) throws IOException {
        return importReport(file, null, null);

    }

    public String importReport(String file, Container container, Executor executor) throws IOException {
        setAutoCreate(true);
        initialize();
        Map<TestCase, Note> triageMap = new HashMap<>();
        Build build = null;
        User user = null;
        TestExecution testExecution = null;
        TestTriage testTriage = new TestTriage();
        try {

            String base64String = file.substring(file.indexOf(",") + 1);
            byte[] decodedString = Base64.getDecoder().decode(base64String.getBytes());


            InputStream myInputStream = new ByteArrayInputStream(decodedString);

            Workbook workbook = new XSSFWorkbook(myInputStream);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            iterator.next();

            TestCase testCase = null;

            Product product = null;
            Note note = null;

            if (executor != null) {
                container = executor.getContainer();
                product = executor.getProduct();
            }

            while (iterator.hasNext()) {
                testCaseService.cleanNewTests();

                Row currentRow = iterator.next();

                if (isValid(currentRow)) {
                    log("Import: Processing row nro ", String.valueOf(currentRow.getRowNum()));

                    String productText = getStringCell(currentRow.getCell(posProduct), null, false);
                    String containerText = getStringCell(currentRow.getCell(posContainer), null, false);
                    String suiteName = getStringCell(currentRow.getCell(posSuiteName), null, false);
                    String testName = getStringCell(currentRow.getCell(posTestName), null, false);
                    String testPath = getStringCell(currentRow.getCell(posTestPath), null, false);
                    String triageNotes = getStringCell(currentRow.getCell(posTriageNotes), null, false);
                    String executionDetail = getStringCell(currentRow.getCell(posExecutionDetail), null, false);
                    long executionDate = getTimestamp(currentRow.getCell(posExecutedTime), DateUtils.now());
                    String statusText = getStringCell(currentRow.getCell(posStatus), null, false);
                    String severityText = getStringCell(currentRow.getCell(posSeverity), null, false);
                    String errorDescription = getStringCell(currentRow.getCell(posErrorDescription), null, false);
                    String stackTrace = getStringCell(currentRow.getCell(posStackTrace), null, false);
                    String standardOutput = getStringCell(currentRow.getCell(posStandardOutput), null, false);
                    String userEmail = getStringCell(currentRow.getCell(posUser), null, false);

                    user = getUser(user, userEmail);

                    if (build == null) {
                        // This is First Row with Data
                        product = getProduct(product, container, productText);
                        container = getContainer(user, product, container, executor, file, containerText);
                        executor = getExecutor(container, executor, file, suiteName);

                        build = DataProvider.getBuild();
                        build.setExecutor(executor);
                        Build lastBuild = buildService.getLastBuild(executor);
                        int buildNumber = 1;
                        if (lastBuild != null) {
                            buildNumber = lastBuild.getNumber() + 1;
                        }
                        build.setNumber(buildNumber);
                        build.setContainer(container);
                        build.setExecutedDate(executionDate);
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
        } catch (FileNotFoundException e) {
            log(Level.SEVERE, "Error, file not found", e);
        } catch (IOException e) {
            log(Level.SEVERE, "Error accessing file", e);
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
                        testTriage = tt;
                        testTriageService.update(tt);
                    }
                }
                jiraAutomationService.createJiraTicket(testExecution, testTriage);
                log("Import: Completed Successfully");
            }
        }

       // jiraAutomationService.createJiraTicket(testCase);

        return logs.toString();

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

        List<String> critical = Arrays.asList(new String[]{"CRITICAL", "SEVERE", "HIGH", "P1", "P0", "S1", "S0", "BLOCKER", "MUST"});
        List<String> medium = Arrays.asList(new String[]{"IMPORTANT", "MEDIUM", "DEFAULT", "P2", "S2"});
        List<String> low = Arrays.asList(new String[]{"USEFUL", "LOW", "P3", "P4", "S3", "S4", "NEGLIGIBLE"});

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

        List<String> fail = Arrays.asList(new String[]{"FAIL", "CANCEL", "SEVERE", "ERROR", "FALSE", "EXCEPTION"});
        List<String> pass = Arrays.asList(new String[]{"PASS", "WORK", "OK", "TRUE", "VALID"});
        List<String> skip = Arrays.asList(new String[]{"SKIP", "INVALID"});

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

    public String getStringCell(Cell param, String defaultValue, boolean uppercase) {
        if (param == null) {
            return defaultValue;
        } else {
            String value = null;
            try {
                // Try to get a string value in spite of the column type
                if (value == null && param.getCellType() == CellType.STRING) {
                    value = String.valueOf(param.getStringCellValue());
                } else if (value == null && param.getCellType() == CellType.NUMERIC) {
                    value = String.valueOf(param.getNumericCellValue());
                    if (value.length() > 2 && ".0".equals(value.substring(value.length() - 2))) {
                        value = value.substring(0, value.length() - 2);
                    }
                } else if (value == null && param.getCellType() == CellType.BOOLEAN) {
                    value = String.valueOf(param.getBooleanCellValue());
                } else if (value == null && param.getCellType() == CellType.FORMULA) {
                    value = String.valueOf(param.getCellFormula());
                } else if (value == null && param.getCellType() == CellType.ERROR) {
                    value = String.valueOf(param.getErrorCellValue());
                } else if (value == null) {
                    value = param.getStringCellValue();
                }

            } catch (IllegalStateException ex) {
                log(Level.SEVERE, String.format("Failed converting cell to string: %s error: %s", value, ex.getLocalizedMessage()), ex);
                throw ex;
            }
            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }
            value = value.trim();
            if (uppercase) {
                value = value.toUpperCase();
            }
            return value;
        }
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
                    if (970757752l < timestamp) {
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

    public boolean isValid(Row currentRow) {
        String testName = getStringCell(currentRow.getCell(posTestName), null, false);
        if (StringUtils.isEmpty(testName)) {
            log(String.format("Test Name not found at column %d row: %s", posTestName, currentRow.toString()));
            return false;
        }
        return true;
    }

}
