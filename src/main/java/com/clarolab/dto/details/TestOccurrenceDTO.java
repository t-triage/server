/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto.details;

import com.clarolab.dto.BaseDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.model.detail.TestOccurrence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// sameTestAt

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestOccurrenceDTO extends BaseDTO {

    private Long suiteID;
    private String suiteName;
    private String groupName;
    private TestTriageDTO testTriage;

    public TestOccurrenceDTO(TestOccurrence testOccurrence) {
        this.suiteID = testOccurrence.getSuiteID();
        this.suiteName = testOccurrence.getSuiteName();
    }

}
