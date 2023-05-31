/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.google.common.collect.Lists;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_TEST_CASE;


@Entity
@Table(name = TABLE_TEST_CASE, indexes = {
        @Index(name = "IDX_TESTCASE_NAME", columnList = "enabled,name"),
        @Index(name = "IDX_TESTCASE_AUTOMATION", columnList = "automatedTestIssue_id"),
        @Index(name = "IDX_TESTCASE_TICKET", columnList = "issueTicket_id"),
        @Index(name = "IDX_TESTCASE_PIN", columnList = "pin_id"),
        @Index(name = "IDX_TESTCASE_ALL", columnList = "name, locationPath", unique = false)
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestCase extends Entry {

    @Type(type = "org.hibernate.type.TextType")
    private String name;

    // typical in dataproviders that dont send the parameter identifier = false
    private boolean dataProvider = false;

    private String relatedIssueID;

    @Type(type = "org.hibernate.type.TextType")
    private String locationPath;

    // if the test is Pinned, like a favorite
    @OneToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "pin_id")
    private TestPin pin;

    @OneToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "automatedTestIssue_id")
    private AutomatedTestIssue automatedTestIssue;

    @OneToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "issueTicket_id")
    private IssueTicket issueTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "testCase", fetch = FetchType.LAZY)
    private List<TestComponentRelation> testComponentRelations;

    @Builder
    private TestCase(Long id, boolean enabled, long updated, long timestamp, String name, String locationPath, TestPin pin, AutomatedTestIssue automatedTestIssue, IssueTicket issueTicket, Product product, boolean dataProvider, String relatedIssueID) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.locationPath = locationPath;
        this.pin = pin;
        this.automatedTestIssue = automatedTestIssue;
        this.issueTicket = issueTicket;
        this.product = product;
        this.dataProvider = dataProvider;
        this.relatedIssueID = relatedIssueID;
    }

    public boolean isEqual(TestCase testCase) {
        if (testCase == null) {
            return false;
        }
        if (name.equals(testCase.getName())) {
            if (locationPath == null) {
                return testCase.getLocationPath() == null;
            } else {
                if (testCase.getLocationPath() == null) {
                    return false;
                } else {
                    return locationPath.equals(testCase.getLocationPath());
                }
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return String.format("TestCase(%d, %s, %s)", getId(), getName(), getLocationPath());
    }

    public String getFullName() {
        if (getLocationPath() != null) {
            return getLocationPath() + "-" + getName();
        } else {
            return getName();
        }
    }

    public boolean isHasMultipleEnvironment() {
        if (getProduct() == null) {
            return false;
        }
        return getProduct().isHasMultipleEnvironment();
    }

    public List<AutomatedComponent> getAutomatedComponents() {
        List<AutomatedComponent> answer = new ArrayList<>();
        testComponentRelations = getTestComponentRelations();

        if (testComponentRelations != null) {
            for (TestComponentRelation testComponentRelation : testComponentRelations) {
                answer.add(testComponentRelation.getComponent());
            }
        }
        
        return answer;
    }

    public void initTestComponentRelations() {
        if (getTestComponentRelations() == null)
            this.setTestComponentRelations(Lists.newArrayList());
    }

    public void add(TestComponentRelation testComponentRelation) {
        initTestComponentRelations();
        this.getTestComponentRelations().add(testComponentRelation);
    }
 
}
