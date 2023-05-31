package com.clarolab.logtriage.dto;

import com.clarolab.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorCaseDTO extends BaseDTO {

    String level;
    String path;
    String thread;
    String message;
    String stackTrace;
    List<Long> events;

}
