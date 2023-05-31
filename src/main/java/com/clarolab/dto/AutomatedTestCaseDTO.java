/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomatedTestCaseDTO extends BaseDTO{
    private String name;
    private String locationPath;
    private boolean pin;

    private List<TestTriageDTO> testTriageDTOList = new ArrayList<>();
    private List<AutomatedComponentDTO> automatedComponentDTOList = new ArrayList<>();
}
