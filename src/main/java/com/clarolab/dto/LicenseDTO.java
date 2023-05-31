package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseDTO extends BaseDTO{

    private long creationTime;
    private long expirationTime;
    private boolean free;
    private boolean expired;
    private String licenseCode;

}
