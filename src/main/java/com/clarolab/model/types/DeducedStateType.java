/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The StateType deduced by the rules. This is executed by the TestTriage.
 * Taking the imported jenkins tests and creating TestTriage
 */

@Setter
@Getter
public class DeducedStateType {
    StateType currentState;
    DeducedReasonType reasonType;

    @Builder
    private DeducedStateType(StateType currentState, DeducedReasonType reasonType) {
        this.currentState = currentState;
        this.reasonType = reasonType;
    }

    public String name() {
        return currentState.name();
    }

    public int getRankBase() {
        return currentState.getRankBase();
    }
}
