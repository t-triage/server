/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.IssueType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_ISSUE_TICKET;


@Entity
@Table(name = TABLE_ISSUE_TICKET, indexes = {
        @Index(name = "IDX_ISSUETICKET_SUMMARY", columnList = "summary"),
        @Index(name = "IDX_ISSUETICKET_TESTCASE", columnList = "testCase_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IssueTicket extends Entry {

    private String summary;
    private String url;
    private String component;
    private String file;
    private int priority;
    private int reopenTimes;
    private long dueDate;


    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User assignee;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private IssueType issueType;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "testCase_id")
    private TestCase testCase;

    @Builder
    private IssueTicket(Long id, boolean enabled, long updated, long timestamp, String summary, String url, String component, String file, int priority, int reopenTimes, long dueDate, String description, User assignee, Note note, Product product, IssueType issueType, TestCase testCase) {
        super(id, enabled, updated, timestamp);
        this.summary = summary;
        this.url = url;
        this.component = component;
        this.file = file;
        this.priority = priority;
        this.reopenTimes = reopenTimes;
        this.dueDate = dueDate;
        this.description = description;
        this.assignee = assignee;
        this.note = note;
        this.product = product;
        this.issueType = issueType;
        this.testCase = testCase;
    }


    public boolean isResolved() {
        return issueType.isResolved();
    }

    public boolean isBlocker() {
        return issueType.isBlocker();
    }

    public boolean isStilOpen(){
        return issueType.isOpen() || issueType.isReOpen();
    }

    public String getDisplaySummary() {
        if (summary == null || summary.isEmpty()) {
            return getUrlKey();
        }
        return getSummary();
    }

    public String getUrlKey() {
        if (url == null || url.isEmpty() || !url.contains("/")) {
            return url;
        }
        String[] urlParsed = url.split("/");

        return urlParsed[urlParsed.length - 1];
    }

    // If this status is still valid for new triages
    public boolean shouldPropagateStatus() {
        return !isResolved();
    }
}
