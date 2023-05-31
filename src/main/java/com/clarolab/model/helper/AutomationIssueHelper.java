/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import com.clarolab.model.AutomatedTestIssue;

public final class AutomationIssueHelper {

    private AutomationIssueHelper() { }

    public static String toString(AutomatedTestIssue entity) {
        String trend = entity.getTrend();

        if (trend == null || trend.isEmpty()) {
            return "There isn't any current execution.";
        }

        if (trend.length() == 1) {
            if (trend.equals("1")) {
                return "It is passing, if it keep passing it will be automatically closed.";
            } else {
                return "Fail test";
            }
        }

        if (!trend.contains("1")) {
            return "All test executions have been failing.";
        }



        if (!trend.substring(trend.length() -1).equals("1")) {
            return "It is passing, if it keep passing it will be automatically closed.";
        }

        if (!trend.substring(trend.length() -2).equals("10")) {
            return "It has passed but it is still failing in some sutie. Waiting for this fix.";
        }

        return "Failing test.";
    }
}
