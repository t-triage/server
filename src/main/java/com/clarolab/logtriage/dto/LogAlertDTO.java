package com.clarolab.logtriage.dto;

import com.clarolab.dto.BaseDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LogAlertDTO extends BaseDTO {

    private String sid;
    private String appName;
    private String owner;
    private String host;
    private String url;
    private Long date;
    private Long lastCheck;
    private SearchExecutorDTO searchExecutor;
    private List<Long> events;

}
