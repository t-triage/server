/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.controller.impl.PageableHelper;
import com.clarolab.dto.FilterDTO;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.jira.service.JiraAutomationService;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ManualTestCaseMapper;
import com.clarolab.model.Product;
import com.clarolab.model.TestCase;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.ProductService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.SearchSpecificationUtil.getSearchSpec;

@Component
@Log
public class ManualTestCaseServiceDTO implements BaseServiceDTO<ManualTestCase, ManualTestCaseDTO, ManualTestCaseMapper> {
    @Autowired
    private ManualTestCaseService service;

    @Autowired
    private ProductService productService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private JiraAutomationService jiraAutomationService;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ManualTestStepServiceDTO manualTestStepServiceDTO;

    @Autowired
    private ManualTestCaseMapper mapper;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestCaseServiceDTO manualTestCaseServiceDTO;

    @Autowired
    private ManualCaseTestImportService importer;

    @Autowired
    private FunctionalityService functionalityService;

    @Autowired
    private FunctionalityServiceDTO functionalityServiceDTO;

    @Override
    public TTriageService<ManualTestCase> getService() {
        return service;
    }

    @Override
    public Mapper<ManualTestCase, ManualTestCaseDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ManualTestCase, ManualTestCaseDTO, ManualTestCaseMapper> getServiceDTO() {
        return this;
    }

    public Page<ManualTestCaseDTO> filterList(String[] criteria, Pageable pageable, FilterDTO filters) {
        Long lastExecution = getLastExecutionLong(filters);
        List<ManualTestCase> list = service.findAllFiltered(getSearchSpec(criteria), pageable.getSort(), lastExecution, filters.getOwner(), filters.getTestPlan(), filters.getTechniques(), filters.getNeedsUpdate(), filters.getRequirement(), filters.getName(), filters.getSuite(), filters.getComponent1(), filters.getComponent2(), filters.getComponent3(), filters.getFunctionalityEntity(), filters.getPriority(), filters.getAutomationStatus(), filters.getId(), filters.getExternalId(), filters.getExcludeTestPlan());

        return PageableHelper.getPageable(pageable, list).map(this::convertToDTO);
    }

    @Override
    public ManualTestCaseDTO save(ManualTestCaseDTO dto) throws ServiceException {

        List<ManualTestStepDTO> dtoSteps = dto.getSteps();

        dto.setSteps(Lists.newArrayList());
        ManualTestCase manualTestCase = saveToEntity(dto);

        for (ManualTestStepDTO step: dtoSteps) {
            if (step.getStep().replaceAll("\\s*", "").isEmpty())
                dtoSteps.remove(step);
            else
                step.setTestCaseId(manualTestCase.getId());
        }

        boolean mainStepFound = false;
        ManualTestStep mainStep = null;
        List<ManualTestStep> steps = Lists.newArrayList();
        for (ManualTestStepDTO stepDTO : dtoSteps) {
            ManualTestStep manualTestStep = manualTestStepServiceDTO.convertToEntity(stepDTO);
            if (stepDTO.isMain()) {
                mainStep = manualTestStep;
                mainStepFound = true;
            }
            steps.add(manualTestStep);
        }

        ManualTestStep savedStep;
        for (ManualTestStep step : steps) {
            step.setTestCase(manualTestCase);
            savedStep = manualTestStepService.save(step);
            if (mainStep == step) {
                manualTestCase.setMainStep(savedStep);
            }
            if (!mainStepFound) {
                manualTestCase.setMainStep(savedStep);
            }
            manualTestCase.getSteps().add(savedStep);
        }

        manualTestCase = jiraAutomationService.checkManualTestConfig(manualTestCase,true);

        service.update(manualTestCase);

        return convertToDTO(manualTestCase);

    }

    @Override
    public ManualTestCaseDTO update(ManualTestCaseDTO dto) throws ServiceException {
        String prevAutomationStatus = "";
        Product prevProduct = null;
        if (dto.getId() != null){
            prevAutomationStatus = manualTestCaseService.find(dto.getId()).getAutomationStatus().toString();
            //Get ProductID from DB to check if it changed.
            prevProduct = manualTestCaseService.find(dto.getId()).getProduct();
        }

        List<ManualTestStepDTO> dtoSteps = dto.getSteps()
                .stream()
                .filter(step -> !step.getStep().replaceAll("\\s*", "").isEmpty())
                .collect(Collectors.toList());

        dto.setSteps(dtoSteps);


        // Is a new step? save it
        for (ManualTestStepDTO manualTestStepDTO : dtoSteps) {
            if (manualTestStepDTO.getId() == null) {
                ManualTestStep savedStep = manualTestStepService.save(manualTestStepServiceDTO.convertToEntity(manualTestStepDTO));
                manualTestStepDTO.setId(savedStep.getId());
            }
        }

        ManualTestCase manualTestCase = convertToEntity(dto);

        //Check for jira configuration
        manualTestCase = jiraAutomationService.checkManualTestConfig(manualTestCase,false, prevAutomationStatus, prevProduct);

        List<ManualTestStep> newSteps = manualTestCase.getSteps();
        List<ManualTestStep> allSteps = manualTestStepService.findAllByTest(manualTestCase);
        List<ManualTestStep> stepsToDelete = Lists.newArrayList();

        // Deletes the unmodified steps
        for (ManualTestStep manualTestStep : allSteps) {
            // if not in dtoSteps > delete
            if (!newSteps.contains(manualTestStep)) {
                stepsToDelete.add(manualTestStep);
            }
        }

        for (ManualTestStep manualTestStep : stepsToDelete) {
            //manualTestStep.setTestCase(null);
            manualTestStepService.delete(manualTestStep.getId());
        }

        // Are there steps w/repeated stepOrder?
        List<ManualTestStep> uniqueStepOrder = newSteps
                .stream()
                .filter(distinctByKey(ManualTestStep::getStepOrder))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEqualCollection(uniqueStepOrder, newSteps)) {
            throw new IllegalArgumentException("StepOrder is wrong");
        }

        //Sort by stepOrder & update all the steps
        dtoSteps.sort(Comparator.comparing(ManualTestStepDTO::getStepOrder));

        //assign the mainStep, if not found assign it to the last step
        boolean mainStepFound = false;

        for (ManualTestStepDTO manualTestStepDTO : dtoSteps) {
            if (manualTestStepDTO.isMain()) {
                manualTestCase.setMainStep(manualTestStepService.find(manualTestStepDTO.getId()));
                mainStepFound = true;
            }
            if (!mainStepFound) {
                manualTestCase.setMainStep(manualTestStepService.find(manualTestStepDTO.getId()));
            }
        }

        service.update(manualTestCase);
        return convertToDTO(manualTestCase);
    }

    private Long getLastExecutionLong(FilterDTO filters) {
        Long lastExecutionLong = null;
        String lastExecution = filters.getLastExecution();
        if (lastExecution != null) {
            switch (lastExecution) {
                case "yesterday":
                    lastExecutionLong = DateUtils.offSetDays(-1);
                    break;
                case "last-3-days":
                    lastExecutionLong = DateUtils.offSetDays(-3);
                    break;
                case "last-week":
                    lastExecutionLong = DateUtils.offSetDays(-7);
                    break;
                case "last-2-weeks":
                    lastExecutionLong = DateUtils.offSetDays(-14);
                    break;
                case "last-month":
                    lastExecutionLong = DateUtils.offSetDays(-30);
                    break;
            }
        }
        return lastExecutionLong;
    }

    public List<ManualTestCaseDTO> toAutomate() {
        List<ManualTestCase> manualTestCases = service.findToAutomate();
        List<ManualTestCaseDTO> manualTestCaseDTOS = Lists.newArrayList();
        for (ManualTestCase manualTestCase : manualTestCases) {
            manualTestCaseDTOS.add(mapper.convertToDTO(manualTestCase));
        }
        return manualTestCaseDTOS;
    }

    private static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<String> searchFunctionality(String name) {
        return service.searchFunctionality(name);
    }

    public void importReport(String file) throws IOException {
        importer.setAutoCreate(true);
        importer.initialize();


        try {

            String base64String = file.substring(file.indexOf(",") + 1);
            byte[] decodedString = Base64.getDecoder().decode(base64String.getBytes());


            InputStream myInputStream = new ByteArrayInputStream(decodedString);

            Workbook workbook = new XSSFWorkbook(myInputStream);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            iterator.next();
            int stepId = 1;
            List<ManualTestStepDTO> steps = new ArrayList<>();
            List<ManualTestStepDTO> manualTestSteps = null;
            ManualTestCaseDTO manualTestCaseDTO = null;
            ManualTestCase manualTestCasePersisted = null;
            String externalId = null;
            String previousExternalId = null;

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();

                if (importer.isValid(currentRow)) {

                    externalId = importer.getStringCell(currentRow.getCell(importer.posId), null, false);

                    importer.log("Import: Processing row nro ", String.valueOf(currentRow.getRowNum()));

                    if (!StringUtils.isEmpty(externalId) && externalId.equalsIgnoreCase(importer.defaultsImportRow)) {
                        // This row is about new defaults for import
                        importer.importDefaults(currentRow);
                    } else {

                        if (externalId == null || (externalId.equals(previousExternalId))) {
                            // Enter here when is a new step and the rest of the fields are empty
                            manualTestSteps = importer.importStep(manualTestCasePersisted, currentRow, stepId);

                            if (manualTestSteps != null && !manualTestSteps.isEmpty()) {
                                steps.addAll(manualTestSteps);
                                stepId = stepId + manualTestSteps.size();
                            }
                        } else {
                            if (manualTestCaseDTO != null && !externalId.equals(previousExternalId)) {
                                // enter here when find a new test case, so persists the previous test case
                                manualTestCaseDTO.setSteps(steps);

                                if (manualTestCaseDTO.getId() == null) {
                                    this.save(manualTestCaseDTO);
                                } else {
                                    this.update(manualTestCaseDTO);
                                }

                                stepId = 1;
                                steps = new ArrayList<>();
                            }

                            // Enter here when is the first step and fill all the fields
                            manualTestCasePersisted = manualTestCaseService.findByExternalId(externalId);
                            if (manualTestCasePersisted != null) {
                                manualTestCaseDTO = manualTestCaseServiceDTO.convertToDTO(manualTestCasePersisted);
                            } else {
                                manualTestCaseDTO = new ManualTestCaseDTO();
                                manualTestCaseDTO.setExternalId(externalId);
                            }

                            manualTestCaseDTO = importer.importTest(manualTestCaseDTO, currentRow);
                            manualTestSteps = importer.importStep(manualTestCasePersisted, currentRow, stepId);

                            stepId = stepId + manualTestSteps.size();

                            steps.addAll(manualTestSteps);
                        }
                    }
                    previousExternalId = externalId;
                }
            }

            // When all the test cases finish to be looped, then the last test case is persisted
            manualTestCaseDTO.setSteps(steps);
            if (manualTestCaseDTO.getId() == null) {
                this.save(manualTestCaseDTO);
            } else {
                this.update(manualTestCaseDTO);
            }

        } catch (FileNotFoundException e) {
            importer.log(Level.SEVERE, "Error, file not found", e);
        } catch (IOException e) {
            importer.log(Level.SEVERE, "Error accessing file", e);
        }

        importer.log("Import: Completed");

    }

    public void updateFunctionalities() {
        List<ManualTestCase> manualTestCases = manualTestCaseService.findAllByFunctionalityNotNull();
        if (manualTestCases.isEmpty()) {
            return;
        }
        
        try {

            for (ManualTestCase test : manualTestCases) {
                String functionalityName = test.getFunctionality();

                Functionality functionality = functionalityService.findFunctionalityByName(functionalityName);

                if (functionality == null) {
                    functionality = Functionality.builder()
                            .enabled(true)
                            .externalId("")
                            .name(functionalityName)
                            .risk("")
                            .story("")
                            .build();

                    functionalityService.save(functionality);
                }

                test.setFunctionalityEntity(functionalityServiceDTO.findEntity(functionality.getId()));
                manualTestCaseService.update(test);
            }

        } catch (Exception e) {
            System.out.println("Error converting functionality from String to Entity");
        }
    }

    public void linkedManualTestToAutomatedTest(@Nonnull Long manualTestId,@Nonnull Long automatedTestId){


         ManualTestCase manualTest = manualTestCaseService.find(manualTestId);
         TestCase automatedTestCase = testCaseService.find(automatedTestId);

         if (manualTest != null && automatedTestCase != null) {
             manualTest.setAutomatedTestCase(automatedTestCase);
             manualTestCaseService.save(manualTest);
         } else {
             if (manualTest == null) {
                throw new IllegalArgumentException("Manual test doesn't exists | Id: " + manualTestId);
             } else {
                throw new IllegalArgumentException("Automated test doesn't exists | Id: " + automatedTestId);
             }
         }
    }

    public List<ManualTestCaseDTO> getManualTestCasesSince(long timestamp){
        List<ManualTestCase> list = manualTestCaseService.getManualTestCases(timestamp);
        List<ManualTestCaseDTO> listDto = new ArrayList<>();
        for (ManualTestCase l : list ){
            listDto.add(mapper.convertToDTO(l));
        }
        return listDto;
    }
}
