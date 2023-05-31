/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildTriageDTO extends BaseDTO {

    private String name;
    private int rank;
    private String standardOutputUrl;
    private boolean triaged;
    private UserDTO triager;
    private NoteDTO note;
    private TriageSpecDTO triageSpec;

    private int getDaysToDeadline;
    private int deadlinePriority; // 1 red, 2 yellow, 3 green
    private Long deadline;

    // Info de Tests
    private Long totalTests;
    private Long totalNewFail;
    private Long totalFail;
    private Long totalNowPassing;
    private Long totalPass;
    private Long totalSkip;

    // Deprecated?
    private Long build;
    private Long container;
    private Long executor;
    private String tags;
    private String currentState;
    private String file;
}
