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
public class UserPreferenceDTO extends BaseDTO {

    private UserDTO user;
    private long rowPerPage;
    private long currentPageNUmber;
    private long currentContainer;

}
