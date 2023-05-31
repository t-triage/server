package com.clarolab.jira.model;

public enum DashboardNumeration {

    OPEN  ("11"),

    REOPEN("21"),

    RESOLVED("31"),

    CLOSED("41");


    private final String column;

    DashboardNumeration(String column) {
        this.column=column;
    }

    public String getColumn(){
        return column;
    }
}
