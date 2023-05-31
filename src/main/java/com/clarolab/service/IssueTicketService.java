/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.*;
import com.clarolab.model.types.IssueType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.IssueTicketRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class IssueTicketService extends BaseService<IssueTicket> {

    @Autowired
    private IssueTicketRepository issueTicketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Override
    public BaseRepository<IssueTicket> getRepository() {
        return issueTicketRepository;
    }

    public List<IssueTicket> findAllByAndAssignee(User user) {
        return issueTicketRepository.findAllByAndAssignee(user);
    }

    public List<IssueTicket> findAllByAndAssignee(long userId) {
        User user = userService.find(userId);
        return findAllByAndAssignee(user);
    }

    public IssueTicket find(TestTriage test) {
        return issueTicketRepository.getByTestCase(test.getTestCase());
    }

    public IssueTicket find(TestCase test) {
        return issueTicketRepository.getByTestCase(test);
    }

    public void computeNewTest(TestTriage test, IssueTicket issue) {
        if (test == null || issue == null) {
            return;
        }

        if (test.isPassed() && !issue.isResolved()) {
            // The test is now passing, it means no product bug. closing the issue.
            setFix(test, issue);
        }

        if (test.isFailed() && issue.isResolved()) {
            // Reopens the ticket has failed again
            issue.setReopenTimes(issue.getReopenTimes() + 1);
            issue.setIssueType(IssueType.REOPEN);
            save(issue);
            notifyIssueReopened(test, issue);
        }
    }

    public boolean testTriageCreated(TestTriage testTriage) {
        boolean dbUpdate = false;

        // Checks if there was a product ticket filed for the test
        // IssueTicket ticket = issueTicketService.find(testTriage);
        IssueTicket ticket = testTriage.getTestCase().getIssueTicket();
        if (ticket != null) {
            // Let's notify since it may be updated.
            computeNewTest(testTriage, ticket);
            dbUpdate = true;
        }

        return dbUpdate;
    }

    private void notifyIssueReopened(TestTriage test, IssueTicket issue) {
        createEvent(test, issue, ApplicationEventType.ISSUE_REOPENED_AUTOMATICALLY);
    }

    private void notifyIssueResolved(TestTriage test, IssueTicket issue) {
        createEvent(test, issue, ApplicationEventType.ISSUE_RESOLVED_AUTOMATICALLY);
    }

    private void createEvent(TestTriage test, IssueTicket issue, ApplicationEventType eventType) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(eventType);
        event.setSource(issue);
        event.setParameter(test);
        event.setExtraParameter(String.format("Issue: %s, was %s since test: %s has now: %s",
                issue.getDisplaySummary(),
                eventType.toString(),
                test.getTestName(),
                test.getCurrentStateName()));
        applicationEventBuilder.save(event);
    }


    public IssueTicket triage(IssueTicket newTicket) {
        IssueTicket answer = null;

        if (newTicket.getId() == null) {
            answer = save(newTicket);
        } else {
            answer = update(newTicket);
        }

        if (answer.getTestCase().getIssueTicket() == null) {
            answer.getTestCase().setIssueTicket(answer);
            testCaseService.update(answer.getTestCase());
        }

        return answer;
    }

    public void updateProductIssue(TestTriage triage, boolean hasNowProductBug, boolean hadProductBug) {
        if (hadProductBug && !hasNowProductBug) {
            // It used to have a bug
            IssueTicket issue = triage.getTestCase().getIssueTicket();
            if (issue != null) {
                if (triage.isProductWorking()) {
                    setFix(triage, issue);
                    triage.getTestCase().setIssueTicket(null);
                    testCaseService.update(triage.getTestCase());
                } else {
                    // it is an Skip, dont do anything
                }
            }
        }
    }

    private void setFix(TestTriage triage, IssueTicket issue) {
        issue.setIssueType(IssueType.FIXED);
        save(issue);
        notifyIssueResolved(triage, issue);
    }

    public List<IssueTicket> getProductIssues(Product product){
        return issueTicketRepository.findAllByProduct(product);
    }

    public List<IssueTicket> getProductIssues(Product product, Long startDate,  Long endDate){
        return issueTicketRepository.findAllByProduct(product, startDate, endDate);
    }
}
