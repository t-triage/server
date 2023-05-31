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
public class ConnectorDTO extends BaseDTO {

    private String name;
    private String url;
    private String userName;
    private String userToken;
    private String type;
    private List<Long> containers;
}
