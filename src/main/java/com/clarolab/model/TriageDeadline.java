/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.util.DateUtils;
import com.clarolab.view.SuiteView;
import lombok.Builder;
import lombok.Getter;

import static com.clarolab.util.Constants.DEFAULT_MAX_TESTCASES_PER_DAY;

// This class intends to be a helper object to handle deadline
// This is not persistent
@Getter
public class TriageDeadline {
    private TriageSpec spec;
    private long deadline;
    private long toTriage;
    private SuiteView view;

    // Computed values
    private int daysToDeadline;
    private int deadlinePriority;
    private String deadlineTooltip;


    @Builder
    private TriageDeadline(TriageSpec spec, long deadline, long toTriage, SuiteView view) {
        this.spec = spec;
        this.deadline = deadline;
        this.toTriage = toTriage;
        this.view = view;

        setDaysToDeadline();
        setPriorityAndTooltip();
    }

    private void setDaysToDeadline() {
        daysToDeadline = DateUtils.daysFromToday(getDeadline());
    }

    private void setPriorityAndTooltip() {
        int daysToDeadline = Math.min(3, getDaysToDeadline());
        int testBase = (int) DEFAULT_MAX_TESTCASES_PER_DAY; // divided like pending jobs
        int pendingToTriage = (int) getToTriage();

        if (view != null && view.isTriaged()) {
            deadlineTooltip = "Suite Triage Done";
            deadlinePriority = 5;
            return;
        }
        if (daysToDeadline < 0) {
            deadlineTooltip = "Suite expected analysis time has expired";
            deadlinePriority = 1;
            return;
        }
        if (daysToDeadline > 3) {
            deadlineTooltip = "You are good";
            deadlinePriority = 4;
            return;
        }
        switch (daysToDeadline) {
            case 0:
                if (pendingToTriage > testBase) {
                    deadlineTooltip = "There are several of tests to validate for today";
                    deadlinePriority = 1;
                } else {
                    if (pendingToTriage > testBase / 2) {
                        deadlineTooltip = "There are some tests to validate for today";
                        deadlinePriority = 2;
                    } else {
                        if (pendingToTriage > 0) {
                            deadlineTooltip = "There are few tests to validate for today";
                            deadlinePriority = 3;
                        } else {
                            deadlineTooltip = "No pending tests, just go to Suite Summary > Triage Suite";
                            deadlinePriority = 4;
                        }

                    }
                }
                break;
            case 1:
                if (pendingToTriage > testBase * 1.5) {
                    deadlineTooltip = "There are lot of tests to validate even and not that much time";
                    deadlinePriority = 1;
                } else {
                    if (pendingToTriage > testBase) {
                        deadlineTooltip = "There are some tests to validate";
                        deadlinePriority = 1;
                    } else {
                        if (pendingToTriage > 0) {
                            deadlineTooltip = "There are few tests to validate and enough time";
                            deadlinePriority = 3;
                        } else {
                            deadlineTooltip = "No pending tests, just go to Suite Summary > Triage Suite";
                            deadlinePriority = 4;
                        }
                    }
                }
                break;
            case 2:
                if (pendingToTriage > testBase * 3) {
                    deadlineTooltip = "There are lot of tests to validate even there is time";
                    deadlinePriority = 1;
                } else {
                    if (pendingToTriage > testBase * 1.5) {
                        deadlineTooltip = "There are some tests to validate";
                        deadlinePriority = 1;
                    } else {
                        if (pendingToTriage > 0) {
                            deadlineTooltip = "There are few tests to validate and enough time";
                            deadlinePriority = 3;
                        } else {
                            deadlineTooltip = "No pending tests, just go to Suite Summary > Triage Suite";
                            deadlinePriority = 4;
                        }
                    }
                }
                break;
            default:
                deadlineTooltip = "Just keep it in mind";
                deadlinePriority = 4;
        }
    }
}
