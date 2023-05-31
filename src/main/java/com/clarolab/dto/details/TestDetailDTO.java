/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto.details;

import com.clarolab.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDetailDTO extends BaseDTO {

    private long consecutiveFails;
    private long historicPasses;
    private long failsSince;

    private long consecutivePasses;
    private long historicFails;
    private long passSince;

    @JsonProperty("sameErrorsAt")
    private List<ErrorOccurrenceDTO> errorOccurrenceDTOS;

    @JsonProperty("sameTestAt")
    private List<TestOccurrenceDTO> testOccurrenceDTOS;


}
