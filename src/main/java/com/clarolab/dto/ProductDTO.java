/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO extends BaseDTO {

    private String name;
    private String description;
    private String logo;
    private String packageNames;
    private String logPattern;
    private List<Long> deadlines;
    private List<Long> containers;
    private List<Long> repositories;
    private NoteDTO note;
    private boolean hasMultipleEnvironment;
}
