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
public class ContainerDTO extends BaseDTO {

    private String name;
    private String productName;
    private String description;
    private String hiddenData;
    private String url;
    private String type;
    private List<Long> executors;
    private Long connector;
    private Long product;
    private String populateMode;
    private int pendingBuildTriages;
    private boolean isLoggedOwner;
    private TriageSpecDTO triageSpec;
    private String reportType;

}
