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
public class UpdateTriageDTO extends BaseDTO {

    private TestTriageDTO testTriageDTO;
    private IssueTicketDTO issueTicketDTO;
    private AutomatedTestIssueDTO automatedTestIssueDTO;
    private UserDTO loggedUserDTO;

    public void markTriaged() {
        testTriageDTO.setTriager(loggedUserDTO);
        testTriageDTO.setTriaged(true);
    }
}
