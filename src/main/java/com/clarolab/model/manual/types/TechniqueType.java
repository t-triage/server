/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.types;

public enum TechniqueType {

    HAPPY_PATH(0),
    EXPLORATORY(1),

    BLACK_BOX(2),
    GREY_BOX(3),
    WHITE_BOX(4),

    BOUNDARY(5),
    CRUD(6),

    POSITIVE(7),
    NEGATIVE(8),

    EXCEPTION(9),
    SYNTAX(10),
    BUSINESS_RISK(11),

    PERFORMANCE(12),
    SECURITY(13),
    DATABASE(14),
    UNDEFINED(15),
    USABILITY(16);

    private final int type;

    TechniqueType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }



}
