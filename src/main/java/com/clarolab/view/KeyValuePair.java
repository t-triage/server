/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KeyValuePair {

    private String key;
    private Object value;
    private String description;

    @Builder
    private KeyValuePair(String key, Object value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }
}
