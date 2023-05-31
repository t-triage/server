package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.dto.NoteDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.impl.FunctionalityMapper;
import com.clarolab.mapper.impl.NoteMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.Product;
import com.clarolab.model.User;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.model.manual.service.ProductComponentService;
import com.clarolab.model.manual.types.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.ProductService;
import com.clarolab.service.UserService;
import com.clarolab.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Component
@Log
@Setter
@Getter
public class ManualCaseTestImportService {
    public final String defaultsImportRow = "t-TriageDefaults";
    Product defaultProduct = null;
    Functionality defaultFunctionality = null;
    ProductComponent defaultComponent = null;
    String defaultSuiteType = null;
    String defaultPriority = null;
    String defaultAutomationType = null;
    String defaultTechnique = null;

    private boolean autoCreate = false;
    StringBuffer logs;
    public final String dontParse = "RAW";
    public final String uniqueValues = "UNIQUE";
    public boolean splitSteps = true;
    public boolean uniqueTestName = false;

    @Autowired
    private ProductService productService;

    @Autowired
    private FunctionalityService functionalityService;
    
    @Autowired
    private FunctionalityMapper functionalityMapper;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private ManualTestStepServiceDTO manualTestStepServiceDTO;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private AuthContextHelper authContextHelper;
    
    @Autowired
    private ManualTestCaseService manualTestCaseService;

    final int posId = 0;
    final int posProduct = 1;
    final int posSuite = 2;
    final int posTestName = 3;
    final int posRequirement = 4;
    final int posStep = 5;
    final int posExpected = 6;
    final int posData = 7;
    final int posPriority = 8;
    final int posTechniques = 9;
    final int posComponents = 10;
    final int posFunctionality = 11;
    final int posAutomation = 12;
    final int posAuthor = 13;
    final int posLastUpdater = 14;
    final int posLastExecution = 15;
    final int posNote = 16;

    public void initialize() {

        // Maintains a list of notifications performed during the import.
        logs = new StringBuffer();

        // Set a default product if it is possible
        defaultProduct = null;
        List<Product> products = productService.findAll();
        if (!products.isEmpty()) {
            defaultProduct = products.get(0);
        }
    }

    protected String getValidPriority(Cell priorityCell) {
        String defaultValue = TestPriorityType.LOW.name();
        if (defaultPriority != null) {
            defaultValue = defaultPriority;
        }
        String value = getStringCell(priorityCell, defaultValue, true);
        if (isKindOf(TestPriorityType.LOW, value)) {
            return value;
        }

        List<String> critical = Arrays.asList(new String[]{"CRITICAL", "SEVERE", "HIGH", "P1", "P0", "S1", "S0", "BLOCKER", "MUST"});
        List<String> medium = Arrays.asList(new String[]{"IMPORTANT", "MEDIUM", "DEFAULT", "P2", "S2"});
        List<String> low = Arrays.asList(new String[]{"USEFUL", "LOW", "P3", "P4", "S3", "S4"});

        if (critical.contains(value)) {
            return TestPriorityType.HIGH.name();
        }
        if (medium.contains(value)) {
            return TestPriorityType.MEDIUM.name();
        }
        if (low.contains(value)) {
            return TestPriorityType.LOW.name();
        }

        return defaultValue;
    }

    protected String getValidSuite(Cell priorityCell) {
        String defaultValue = SuiteType.REGRESSION.name();
        if (defaultSuiteType != null) {
            defaultValue = defaultSuiteType;
        }
        String value = getStringCell(priorityCell, defaultValue, true);
        if (isKindOf(SuiteType.REGRESSION, value)) {
            return value;
        }

        List<String> critical = Arrays.asList(new String[]{"CRITICAL", "SEVERE", "HIGH", "P1", "P0", "S1", "S0", "BLOCKER", "MUST"});
        List<String> medium = Arrays.asList(new String[]{"IMPORTANT", "MEDIUM", "DEFAULT", "P2", "S2"});
        List<String> feature = Arrays.asList(new String[]{"FEATURE", "FEATURE REGRESSION"});
        List<String> release = Arrays.asList(new String[]{"RELEASE", "RELEASE REGRESSION"});

        if (critical.contains(value)) {
            return SuiteType.SMOKE.name();
        }
        if (medium.contains(value)) {
            return SuiteType.REGRESSION.name();
        }
        if (feature.contains(value)) {
            return SuiteType.FEATURE.name();
        }
        if (release.contains(value)) {
            return SuiteType.RELEASE.name();
        }

        return defaultValue;
    }

    protected List<String> getTechniques(Cell cell) {
        List<String> techniques = new ArrayList<>();
        String technique = getValidTechnique(cell);
        if (technique != null) {
            techniques.add(technique);
        }

        return techniques;
    }

    protected String getValidTechnique(Cell priorityCell) {
        String value = getStringCell(priorityCell, null, true);
        if (isKindOf(TechniqueType.HAPPY_PATH, value)) {
            return value;
        }
        String defaultValue = TechniqueType.HAPPY_PATH.name();
        if (defaultTechnique != null) {
            defaultValue = defaultTechnique;
        }

        List<String> happy = Arrays.asList(new String[]{"SMOKE", "EASY", "DEFAULT", "HAPPY", "BASE", "BASIC", "IMPORTANT"});
        List<String> positive = Arrays.asList(new String[]{"REGRESSION", "MEDIUM"});

        if (happy.contains(value)) {
            return TechniqueType.HAPPY_PATH.name();
        }
        if (positive.contains(value)) {
            return TechniqueType.POSITIVE.name();
        }

        return defaultValue;
    }

    protected String getValidAutomationStatus(Cell priorityCell) {
        String defaultValue = AutomationStatusType.NO.name();
        if (defaultAutomationType != null) {
            defaultValue = defaultAutomationType;
        }

        String value = getStringCell(priorityCell, defaultValue, true);
        if (isKindOf(AutomationStatusType.NO, value)) {
            return value;
        }

        List<String> done = Arrays.asList(new String[]{"AUTOMATED", "AUTOMATION","DONE", "COMPLETED", "YES"});
        List<String> manual = Arrays.asList(new String[]{"NO", "MANUAL", "PENDING", "MUST"});
        List<String> critical = Arrays.asList(new String[]{"CRITICAL", "SEVERE", "HIGH", "P1", "P0"});
        List<String> medium = Arrays.asList(new String[]{"IMPORTANT", "MEDIUM", "DEFAULT", "P2"});
        List<String> low = Arrays.asList(new String[]{"USEFUL", "LOW", "P3", "P4"});
        List<String> no = Arrays.asList(new String[]{"NO AUTOMATABLE"});

        if (done.contains(value)) {
            return defaultValue;
        }
        if (manual.contains(value)) {
            return defaultValue;
        }
        if (critical.contains(value)) {
            return AutomationStatusType.PENDING_HIGH.name();
        }
        if (medium.contains(value)) {
            return AutomationStatusType.PENDING_MEDIUM.name();
        }
        if (low.contains(value)) {
            return AutomationStatusType.PENDING_LOW.name();
        }
        if (no.contains(value)) {
            return AutomationStatusType.NO.name();
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
                    if (value.length() > 2 && ".0".equals(value.substring(value.length() -2))) {
                        value = value.substring(0, value.length() -2);
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

    public Long getNumericCell(Cell param, long defaultValue, boolean zeroValid) {
        if (param == null) {
            return defaultValue;
        } else {
            Long value = null;
            try {
                // Try to get a string value in spite of the column type
                if (value == null && param.getCellType() == CellType.NUMERIC) {
                    value = (long) param.getNumericCellValue();
                } else if (value == null) {
                    value = Long.parseLong(param.getStringCellValue());
                }
            } catch (IllegalStateException ex) {
                log(Level.SEVERE, String.format("Failed converting cell to number: %s error: %s", value, ex.getLocalizedMessage()), ex);
                throw ex;
            }
            if (value == null) {
                return defaultValue;
            }
            if (value == 0 && !zeroValid) {
                return defaultValue;
            }

            return value;
        }
    }

    public UserDTO getUserDTOLike(Cell nameParam) {
        String name = getStringCell(nameParam, null, false);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        List<User> users = userService.search(name);

        if (users.size() > 0) {
            return userMapper.convertToDTO(users.get(0));
        } else {
            return null;
        }
    }

    public Product getProduct(Cell nameParam) {
        String productName = getStringCell(nameParam, null, false);
        Product product = productService.findProductByName(StringUtils.prepareStringForSearch(productName));
        if (product == null && !StringUtils.isEmpty(productName) && autoCreate) {
            product = DataProvider.getProduct();
            product.setName(productName.trim());
            product.setDescription("Created at Import");
            product = productService.save(product);
        }
        if (product == null) {
            product = defaultProduct;
        }

        return product;

    }

    public Functionality getFunctionality(Cell nameParam){
        String functionalityName = getStringCell(nameParam, null, false);
        Functionality functionality = functionalityService.findFunctionalityByName(StringUtils.prepareStringForSearch(functionalityName));
        if(functionality == null && !StringUtils.isEmpty(functionalityName) && autoCreate){
            functionality = DataProvider.getFunctionality();
            functionality.setName(functionalityName.trim());
            functionality = functionalityService.save(functionality);
        }

        if(functionality == null){
            functionality = defaultFunctionality;
        }

        return functionality;
    }

    public Long getTimestamp(Cell dateParam) {
        if (dateParam == null) {
            return null;
        } else {
            try {
                Date date = dateParam.getDateCellValue();
                if (date == null) {
                    return null;
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
        return null;
    }

    public NoteDTO getNoteDTO(Cell cell, UserDTO userDTO) {
        String text = getStringCell(cell, null, false);

        if (text == null) {
            return null;
        }

        NoteDTO note = new NoteDTO();
        note.setDescription(text);
        if (userDTO != null) {
            note.setAuthor(userDTO.getId());
        }

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

    // Answer if the priority is a valid enum
    private boolean isKindOf(Enum<?> enumType, String text) {
        Enum<?>[] enumConstants = (Enum<?>[]) enumType.getClass().getEnumConstants();

        for (Enum<?> type : enumConstants) {
            if (type.name().equals(text)) {
                return true;
            }
        }
        return false;
    }

    public List<ManualTestStepDTO> importStep(ManualTestCase manualTestCasePersisted, Row currentRow, int stepOrder) {
        List<ManualTestStepDTO> testSteps = new ArrayList<>();
        int expectedOrder = 0;
        int foundOrder = 0;
        ManualTestStepDTO step = null;
        String stepCell = getStringCell(currentRow.getCell(posStep), null, false);
        String expectedCell = getStringCell(currentRow.getCell(posExpected), null, false);
        String dataCell = getStringCell(currentRow.getCell(posData), null, false);

        if (splitSteps && stepCell != null && stepCell.contains("\n")) {
            // Parsing one step cell that contains several steps
            List<String> stepList = (stepCell == null) ? new ArrayList() : Arrays.stream(stepCell.split("\n")).map(s -> StringUtils.cleanup(s)).collect(Collectors.toList());
            List<String> expectedList = (expectedCell == null) ? new ArrayList() : Arrays.stream(expectedCell.split("\n")).map(s -> StringUtils.cleanup(s)).collect(Collectors.toList());
            List<String> dataList = (dataCell == null) ? new ArrayList() : Arrays.stream(dataCell.split("\n")).map(s -> StringUtils.cleanup(s)).collect(Collectors.toList());

            for (int i = 0; i < stepList.size(); i++) {
                String stepText = stepList.get(i);
                String expectedText = null;
                String dataText = null;

                // Look for the best expected result for that step
                // First it looks for a step starting with the number of the step.
                String expectedStarting = null;
                int j = 0;
                while (expectedStarting == null && j < expectedList.size()) {
                    if (StringUtils.startsWith(expectedList.get(j), String.valueOf(i + 1))) {
                        expectedStarting = expectedList.get(j);
                    }
                    j ++;
                }
                if (expectedStarting != null) {
                    expectedText = expectedStarting;
                    foundOrder = foundOrder + 1;
                } else {
                    // It will find if there is an associated step in order
                    if ((expectedList.size() - foundOrder) > (stepList.size() - i - 1) && (expectedList.size() - foundOrder) > expectedOrder) {
                        String expectedCandidate = expectedList.get(expectedOrder);
                        if (expectedCandidate != null && expectedCandidate.length() > 0) {
                            // Analyzing if it was not previously used
                            try {
                                String firstChar = expectedCandidate.substring(0,1);
                                int index = Integer.valueOf(firstChar);
                                if (index <= i) {
                                    // The step was already processed (i.e. starts with: 2. step, and now processing step 4)
                                    expectedCandidate = null;
                                }
                            }
                            catch (NumberFormatException e) {
                            }
                            catch (IndexOutOfBoundsException e) {
                            }
                        }
                        expectedText = expectedCandidate;
                    }
                }

                // Look for the best data for that step
                if (dataList.size() > i) {
                    dataText = dataList.get(i);
                }

                step = createStep(manualTestCasePersisted, stepText, expectedText, dataText, stepOrder);

                if (step != null) {
                    testSteps.add(step);
                    stepOrder = stepOrder + 1;

                    if (expectedText != null) {
                        expectedOrder = expectedOrder + 1;
                    }
                }

            }


        } else {
            step = createStep(manualTestCasePersisted, stepCell, expectedCell, dataCell, stepOrder);

            if (step != null) {
                testSteps.add(step);
            }
        }

        return testSteps;

    }

        private ManualTestStepDTO createStep(ManualTestCase manualTestCasePersisted, String step, String expected, String data, int stepOrder) {
            boolean hasData = false;
            ManualTestStep manualTestStepPersisted = manualTestStepService.findStep(manualTestCasePersisted, stepOrder);
            ManualTestStepDTO manualTestStep = null;
            if (manualTestStepPersisted != null) {
                manualTestStep = manualTestStepServiceDTO.convertToDTO(manualTestStepPersisted);
                hasData = true;
            } else {
                manualTestStep = new ManualTestStepDTO();
            }
            manualTestStep.setExternalId(stepOrder);
            manualTestStep.setStep(step);
            manualTestStep.setExpectedResult(expected);
            manualTestStep.setData(data);
            manualTestStep.setStepOrder(stepOrder);
            manualTestStep.setEnabled(true);

            if (!StringUtils.isEmpty(manualTestStep.getStep()) || !StringUtils.isEmpty(manualTestStep.getExpectedResult()) || !StringUtils.isEmpty(manualTestStep.getData())) {
                hasData = true;
            }

            if (hasData) {
                return manualTestStep;
            } else {
                return null;
            }

    }

    public ManualTestCaseDTO importTest(ManualTestCaseDTO manualTestCaseDTO, Row currentRow) {
        Product product = getProduct(currentRow.getCell(posProduct));
        Functionality functionality = getFunctionality(currentRow.getCell(posFunctionality));
        if (product != null) {
            manualTestCaseDTO.setProductName(product.getName());
            manualTestCaseDTO.setProductId(product.getId());

            defaultProduct = product;
        } else {
            manualTestCaseDTO.setProductName(defaultProduct.getName());
            manualTestCaseDTO.setProductId(defaultProduct.getId());
        }

        if (functionality != null){
            manualTestCaseDTO.setFunctionalityEntity(functionalityMapper.convertToDTO(functionality));

            defaultFunctionality = functionality;
        }else{
            manualTestCaseDTO.setFunctionalityEntity(functionalityMapper.convertToDTO(defaultFunctionality));
        }

        manualTestCaseDTO.setSuite(getValidSuite(currentRow.getCell(posSuite)));
        manualTestCaseDTO.setName(getStringCell(currentRow.getCell(posTestName), null, false));
        manualTestCaseDTO.setRequirement(getStringCell(currentRow.getCell(posRequirement), null, false));

        manualTestCaseDTO.setPriority(getValidPriority(currentRow.getCell(posPriority)));
        manualTestCaseDTO.setTechniques(getTechniques(currentRow.getCell(posTechniques)));

        // Components can be separated by ,
        String components = getStringCell(currentRow.getCell(posComponents), "", false);
        int i = 1;
        for (String aComponent : components.split(",")) {
            if (!StringUtils.isEmpty(aComponent)) {
                aComponent = aComponent.trim();
                ProductComponent productComponent = null;
                List<ProductComponent> existingComponents = productComponentService.searchStrict(aComponent);
                if (existingComponents.isEmpty()) {
                    productComponent = new ProductComponent();
                    productComponent.setName(aComponent);
                    productComponent.setProduct(product);
                    productComponent = productComponentService.save(productComponent);
                } else {
                    productComponent = existingComponents.get(0);
                }

                if (i == 1) {
                    manualTestCaseDTO.setComponent1Name(productComponent.getName());
                    manualTestCaseDTO.setComponent1Id(productComponent.getId());
                } else if (i == 2) {
                    manualTestCaseDTO.setComponent2Name(productComponent.getName());
                    manualTestCaseDTO.setComponent2Id(productComponent.getId());
                } else if (i == 3) {
                    manualTestCaseDTO.setComponent3Name(productComponent.getName());
                    manualTestCaseDTO.setComponent3Id(productComponent.getId());
                }
                if (productComponent != null) {
                    defaultComponent = productComponent;
                }
                i++;
            }
        }
        if (defaultComponent != null && (manualTestCaseDTO.getComponent1Name() == null || manualTestCaseDTO.getComponent1Name().isEmpty())) {
            // if no compontent, we will assign the previous assigned.
            manualTestCaseDTO.setComponent1Name(defaultComponent.getName());
            manualTestCaseDTO.setComponent1Id(defaultComponent.getId());
        }

        manualTestCaseDTO.setAutomationStatus(getValidAutomationStatus(currentRow.getCell(posAutomation)));

        UserDTO author = getUserDTOLike(currentRow.getCell(posAuthor));
        manualTestCaseDTO.setOwner(author == null ? authContextHelper.getCurrentUserAsDTO() : author);

        manualTestCaseDTO.setLastUpdater(getUserDTOLike(currentRow.getCell(posLastUpdater)));

        manualTestCaseDTO.setLastExecutionDate(getTimestamp(currentRow.getCell(posLastExecution)));

        manualTestCaseDTO.setNote(getNoteDTO(currentRow.getCell(posNote), manualTestCaseDTO.getOwner()));

        manualTestCaseDTO.setEnabled(true);
        manualTestCaseDTO.setLastExecutionStatus((ExecutionStatusType.UNDEFINED).name());

        return manualTestCaseDTO;
    }

    public void importDefaults(Row currentRow) {
        setSplitSteps(currentRow.getCell(posStep));

        String productName = getStringCell(currentRow.getCell(posProduct), null, false);
        if (productName != null) {
            defaultProduct = getProduct(currentRow.getCell(posProduct));
        }

        String suiteTypeName = getStringCell(currentRow.getCell(posSuite), null, false);
        if (suiteTypeName != null) {
            defaultSuiteType = getValidSuite(currentRow.getCell(posSuite));
        }

        String priorityName = getStringCell(currentRow.getCell(posPriority), null, false);
        if (priorityName != null) {
            defaultPriority = priorityName;
        }

        String techniqueName = getStringCell(currentRow.getCell(posTechniques), null, false);
        if (techniqueName != null) {
            defaultTechnique = techniqueName;
        }

        String automationTypeName = getStringCell(currentRow.getCell(posAutomation), null, false);
        if (automationTypeName != null) {
            defaultAutomationType = automationTypeName;
        }

        String testNameRequired = getStringCell(currentRow.getCell(posTestName), null, true);
        
        if (uniqueValues.equals(testNameRequired)) {
            uniqueTestName = true;
        }
    }

    private void setSplitSteps(Cell param) {
        String split = getStringCell(param, "no", true);
        if (!StringUtils.isEmpty(split) && split.equalsIgnoreCase(dontParse)) {
            splitSteps = false;
        }
    }

    public boolean isValid(Row currentRow) {
        if (uniqueTestName) {
            List<ManualTestCase> similars = manualTestCaseService.searchStrictName(getStringCell(currentRow.getCell(posTestName), null, false));
            return similars.isEmpty();
        }

        return true;
    }

}
