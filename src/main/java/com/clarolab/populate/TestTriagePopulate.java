/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.model.*;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Log
public class TestTriagePopulate implements Cloneable {

    // Test Description
    String testCaseName;
    String errorDetails;
    String errorStackTrace;
    String description;
    String path;
    String suiteName;
    String statusText;
    int buildNumber;
    StateType expectedStatus;
    Map<Integer, StatusType> buildSpec = new HashMap<>();
    Report report;
    // Information about how the current triage is done
    private StateType newTriageStatus;
    private long snooze = 0L;
    private String tag = "";
    private String comment;
    private String bugFiled = "";
    private boolean automationIssue = false;
    private boolean productIssue = false;
    private boolean pin = false;
    private String[] steps;

    // generated model classes
    private TestTriage testTriage;
    private TestExecution testExecution;
    // previous build object
    private TestExecution previousTestExecution;
    private TestTriage previousTestTriage = new TestTriage();

    public static TestTriage getTriageFor(List<TestTriage> triages, TestExecution testExecution) {
        for (TestTriage triage : triages) {
            if (testExecution.getId().equals(triage.getTestExecution().getId())) {
                return triage;
            }
        }
        return null;
    }

    public void setTriage(List<TestTriage> triages) {
        setTestTriage(getTriageFor(triages, this.getTestExecution()));

    }

    public void findAndSetPreviousTriage(List<TestTriage> triages) {
        setPreviousTestTriage(getTriageFor(triages, this.getPreviousTestExecution()));

    }

    public void setNewTestCase(TestExecution newTest) {
        this.setPreviousTestExecution(this.getTestExecution());
        this.setTestExecution(newTest);
    }

    public String getTestDetail() {
        return getTestCaseName() + " Expected: " + getExpectedStatus() +
                " Actual: " + getTestTriage().getCurrentState() + " --- Test Detail: " + getDescription()
                + " --- Previous test status: " + this.getPreviousTestExecution().getStatus()
                + " Previous Triage Status: " + getPreviousTestTriage().getCurrentState();
    }

    public AutomatedTestIssue createAutomatedTicket(TestTriage triage, User user, AutomatedTestIssue issue) {
        AutomatedTestIssue newIssue = issue;

        if (automationIssue && triage.isFail()) {
            if (newIssue == null) {
                // If not, it creates a new automation issue
                newIssue = AutomatedTestIssue.builder()
                        .enabled(true)
                        .testCase(triage.getTestCase())
                        .issueType(IssueType.OPEN)
                        .userFixPriorityType(UserFixPriorityType.AUTOMATIC)
                        .triager(user)
                        .testTriage(triage)
                        .build();
            }
        }

        return newIssue;
    }

    public IssueTicket createProductTicket(TestTriage triage, User user, IssueTicket issue) {
        IssueTicket newIssue = issue;

        if (productIssue && triage.isFail()) {
            if (newIssue == null) {
                // If not, it creates a new automation issue
                newIssue = IssueTicket.builder()
                        .testCase(triage.getTestCase())
                        .url("http://ticket.clarolab.com/triage-023")
                        .issueType(IssueType.OPEN)
                        .assignee(user)
                        .build();
            }
        }

        return newIssue;
    }



    public boolean setPreviousTriageState(User user, UseCaseDataProvider provider) {
        boolean updateTriage = false;

        provider.setUser(user);

        if (this.getPreviousTestTriage() == null) {
            log.warning("This test has no previous triage id: " + this.getPreviousTestExecution().getId());
        }
        if (!this.getBugFiled().isEmpty()) {
            this.getPreviousTestTriage().setFile(this.getBugFiled());
            updateTriage = true;
        }
        if (this.getSnooze() > 0) {
            this.getPreviousTestTriage().setSnooze(this.getSnooze());
            updateTriage = true;
        }
        if (!this.getTag().isEmpty()) {
            this.getPreviousTestTriage().addTag(this.getTag());
            updateTriage = true;
        }
        if (this.getNewTriageStatus() != null) {
            this.getPreviousTestTriage().setCurrentState(this.getNewTriageStatus());
            updateTriage = true;
        }
        if (this.getComment() != null && !this.getComment().isEmpty()) {
            provider.setNote(null);
            provider.getNote().setName(DataProvider.getRandomName(getComment()));
            this.getPreviousTestTriage().setNote(provider.getNote());

            updateTriage = true;
        }
        if (this.isPin()) {
            TestPin pin = DataProvider.getPin();
            pin.setAuthor(provider.getUser());
            this.getPreviousTestTriage().getTestCase().setPin(pin);
            updateTriage = true;
        }

        if (!StringUtils.isEmpty(getTag())) {
            this.getPreviousTestTriage().setTags(TagHelper.addNewTag(this.getPreviousTestTriage().getTags(), getTag()));
            updateTriage = true;
        }

        if (Math.random() < 0.8) {
            provider.setNote(null);
            getPreviousTestTriage().setNote(provider.getNote());

            updateTriage = true;
        }

        this.getPreviousTestTriage().setTriager(user);

        return updateTriage;
    }

    public StatusType getStatusAtBuild(Build build){
        if (build == null) {
            if (buildSpec.isEmpty()) {
                return null;
            } else {
                return buildSpec.get(1);
            }
        } else {
            return buildSpec.get(build.getNumber());
        }
    }

    public static void setBuildSpec(Map<Integer, StatusType> builds, StatusType status, int from, int to) {
        for (int j = from; j <= to; j++) {
            builds.put(j, status);
        }
    }

    public void setAsPass(int from, int to) {
        setAs(StatusType.PASS, from, to);
        setErrorDetails("");
        setErrorStackTrace("");
    }

    public void setAs(StatusType status, int from, int to) {
        TestTriagePopulate.setBuildSpec(buildSpec, status, from, to);
        setBuildSpec(buildSpec);
    }

    public void initializeFail(String prefix) {
        setTestCaseName(DataProvider.getRandomName(prefix, 1));
        setAs(StatusType.FAIL, 0, 10);
    }

    public TestTriagePopulate clone() throws CloneNotSupportedException {
        return (TestTriagePopulate) super.clone();
    }
}
