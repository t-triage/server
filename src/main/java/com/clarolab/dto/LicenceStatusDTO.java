package com.clarolab.dto;

import lombok.Data;

@Data
public class LicenceStatusDTO {

    // Free limits
    long usersLeft;
    long manualTestsLeft;
    long triagedTestsLeft;

    // Premium license info
    boolean expired;







}
