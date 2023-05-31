/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum StateType {

    NEWFAIL(1000, 10),

    FAIL(2000, 20),

    PERMANENT(3000, 50),

    PASS(4000, 0), //Success or pass

    NEWPASS(5000, 0), //After a few fails... is start passing

    SKIP(7000, 0), //Auto triager but skip TAG

    INVALID(8000, 0), //Auto triager and Invalid + Self State Tag

    UNDEFINED(9000, 90),

    NOT_EXECUTED(8001, 80);

    //blocked by
    //filed

    private final int stateType;
    private final int rankBase;

    StateType(int stateType, int rankBase) {
        this.stateType = stateType;
        this.rankBase = rankBase;
    }

    public int getStateType() {
        return stateType;
    }

    public int getRankBase() {
        return rankBase;
    }

    public String triagedName(){
        return "MARKED_AS-" + name();
    }
    
    public boolean isPass() {
        return this == PASS || this == NEWPASS;
    }
}
