/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

public final class PriorityHelper {


    private PriorityHelper() { }

    public static String convertPriority(int priority) {
        if (priority > 3 || priority < 0) {
            return "P4";
        } else {
            return "P" + priority;
        }
    }

    public static int convertPriority(String priority) {
        String value = priority.toUpperCase();

        int priorityEntity = 99;

        switch (value) {
            case "P0":
                priorityEntity = 0;
                break;
            case "P1":
                priorityEntity = 1;
                break;
            case "P2":
                priorityEntity = 2;
                break;
            case "P3":
                priorityEntity = 3;
                break;
            case "P4":
                priorityEntity = 4;
                break;
        }

        return priorityEntity;
    }
}
