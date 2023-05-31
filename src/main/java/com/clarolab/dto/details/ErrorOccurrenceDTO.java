/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto.details;

import com.clarolab.dto.BaseDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.model.detail.ErrorOccurrence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Same errors at

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorOccurrenceDTO extends BaseDTO {

    private Long suiteID;
    private String suiteName;
    private String groupName;
    private Long testID;
    private String testName;
    private String displayName;
    private TestTriageDTO testTriage;

    public ErrorOccurrenceDTO(ErrorOccurrence errorOccurrence) {
        this.suiteID = errorOccurrence.getSuiteID();
        this.suiteName = errorOccurrence.getSuiteName();
        this.testID = errorOccurrence.getTestID();
        this.testName = errorOccurrence.getTestName();
    }

}
