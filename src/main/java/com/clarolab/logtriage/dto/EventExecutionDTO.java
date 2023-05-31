package com.clarolab.logtriage.dto;

import com.clarolab.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventExecutionDTO extends BaseDTO {

    String content;
    String host;
    String source;
    String sourceType;
    Long date;
    Long indexedTime;
    LogAlertDTO alert;
    ErrorCaseDTO errorCase;

}
