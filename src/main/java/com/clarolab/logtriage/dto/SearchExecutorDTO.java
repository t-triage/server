package com.clarolab.logtriage.dto;

import com.clarolab.dto.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class SearchExecutorDTO extends BaseDTO {

    private String name;
    private String search;
    private String url;
    private String pattern;
    private String packageNames;
    private Long logConnector;
    private List<Long> logAlerts;

}
