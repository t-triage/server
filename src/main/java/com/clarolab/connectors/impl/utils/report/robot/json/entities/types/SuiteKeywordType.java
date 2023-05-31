/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities.types;

public enum SuiteKeywordType {

    KEYWORD("kw"),
    SETUP("setup"),
    TEARDOWN("teardown"),
    FOR("for"),
    FORITEM("foritem");

    private final String type;

    SuiteKeywordType(String type) {
        this.type = type;
    }

    public static SuiteKeywordType getType(String type) {
        switch (type) {
            case "kw":
                return SuiteKeywordType.KEYWORD;
            case "setup":
                return SuiteKeywordType.SETUP;
            case "teardown":
                return SuiteKeywordType.TEARDOWN;
            case "for":
                return SuiteKeywordType.FOR;
            case "foritem":
                return SuiteKeywordType.FORITEM;
            default:
                return null;
        }
    }
}
