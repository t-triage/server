/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *
 * This is the actual test case that should be tested manually.
 * It also works as input to create a new automation test (that once is created it is associated to automatedTestCase).
 * Contains lot of fields that classify the test so it can be searched by that.
 *
 */

package com.clarolab.model.manual;

import com.clarolab.model.*;
import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.SuiteType;
import com.clarolab.model.manual.types.TechniqueType;
import com.clarolab.model.manual.types.TestPriorityType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_CASE;
import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_TECHNIQUE;

@Entity
@Table(name = TABLE_MANUAL_TEST_CASE, indexes = {
        @Index(name = "IDX_MANUAL_TEST_CASE_UPDATE", columnList = "enabled,needsUpdate"),
        @Index(name = "IDX_MANUAL_TEST_CASE_PRIORITY", columnList = "enabled,priority"),
        @Index(name = "IDX_MANUAL_TEST_CASE_PRODUCT", columnList = "enabled,product_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_SUITE", columnList = "enabled,suite"),
        @Index(name = "IDX_MANUAL_TEST_CASE_AUTOMATION", columnList = "enabled,automationStatus"),
        @Index(name = "IDX_MANUAL_TEST_CASE_OWNER", columnList = "enabled,user_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_UPDATER", columnList = "enabled,userUpdate_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP1", columnList = "component1_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP2", columnList = "component2_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP3", columnList = "component3_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP4", columnList = "component4_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP5", columnList = "component5_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_COMP6", columnList = "component6_id"),
        @Index(name = "IDX_MANUAL_TEST_CASE_AUTOMATED", columnList = "enabled,automatedTestCase_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestCase extends Entry {

    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private ManualTestRequirement requirement; // preConditions

    private boolean needsUpdate = false;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    private List<ManualTestStep> steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainStep_id")
    private ManualTestStep mainStep;

    // Test Classification
    private TestPriorityType priority;

    @ElementCollection(targetClass = TechniqueType.class)
    @Enumerated(EnumType.ORDINAL)
    @CollectionTable(name = TABLE_MANUAL_TEST_TECHNIQUE)
    @Column(name = "type", nullable = false)
    private Collection<TechniqueType> techniques;

    // Classification by functionality
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private SuiteType suite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component1_id")
    private ProductComponent component1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component2_id")
    private ProductComponent component2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component3_id")
    private ProductComponent component3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component4_id")
    private ProductComponent component4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component5_id")
    private ProductComponent component5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component6_id")
    private ProductComponent component6;

    private String functionality; // Jira ticket or new feature
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id")
    private Functionality functionalityEntity; // Jira ticket or new feature

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userUpdate_id")
    private User lastUpdater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userAutomation_id")
    private User automationAssignee;

    // WORKFLOW

    private AutomationStatusType automationStatus;

    // Further assignments with the last execution in order to show it fast.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastExecution_id")
    private ManualTestExecution lastExecution;

    // Once the test case is automated it gets related.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automatedTestCase_id")
    private TestCase automatedTestCase;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    private String externalId;

    private String automationExternalId;

    @Builder
    private ManualTestCase(Long id, boolean enabled, long updated, long timestamp, String name, ManualTestRequirement requirement, boolean needsUpdate, List<ManualTestStep> steps, ManualTestStep mainStep, TestPriorityType priority, Collection<TechniqueType> techniques, Product product, SuiteType suite, ProductComponent component1, ProductComponent component2, ProductComponent component3, String functionality, Functionality functionalityEntity, User owner, User lastUpdater, User automationAssignee, AutomationStatusType automationStatus, ManualTestExecution lastExecution, TestCase automatedTestCase, Note note, String externalId, String automationExternalId) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.requirement = requirement;
        this.needsUpdate = needsUpdate;
        this.steps = steps;
        this.mainStep = mainStep;
        this.priority = priority;
        this.techniques = techniques;
        this.product = product;
        this.suite = suite;
        this.note = note;
        this.component1 = component1;
        this.component2 = component2;
        this.component3 = component3;
        this.functionality = functionality;
        this.functionalityEntity = functionalityEntity;
        this.owner = owner;
        this.lastUpdater = lastUpdater;
        this.automationAssignee = automationAssignee;
        this.automationStatus = automationStatus;
        this.lastExecution = lastExecution;
        this.automatedTestCase = automatedTestCase;
        this.externalId = externalId;
        this.automationExternalId = automationExternalId;
    }

    public void addStep(ManualTestStep manualTestStep) {
        steps.add(manualTestStep);
        manualTestStep.setTestCase(this);
    }

    public void removeStep(ManualTestStep manualTestStep) {
        manualTestStep.setTestCase(null);
        this.steps.remove(manualTestStep);
    }
    
    public List<ProductComponent> getComponents() {
        List<ProductComponent> answer = new ArrayList<>();
        if (component1 != null) {
            answer.add(component1);
        }
        if (component2 != null) {
            answer.add(component2);
        }
        if (component3 != null) {
            answer.add(component3);
        }
        if (component4 != null) {
            answer.add(component4);
        }
        if (component5 != null) {
            answer.add(component5);
        }
        if (component6 != null) {
            answer.add(component6);
        }
        
        return answer;
    }
}
