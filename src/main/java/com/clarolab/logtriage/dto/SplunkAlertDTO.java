package com.clarolab.logtriage.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SplunkAlertDTO {

    String app;
    String owner;
    String events;
    String serverHost;
    String serverUri;
    String sessionKey;
    String sid;
    String searchName;
    String pattern;
    String packageNames;
    Long alertTime;

}
