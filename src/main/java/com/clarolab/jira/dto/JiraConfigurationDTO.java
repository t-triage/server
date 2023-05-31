package com.clarolab.jira.dto;

import com.clarolab.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JiraConfigurationDTO extends BaseDTO {

    private int index;
    private String state;

}
