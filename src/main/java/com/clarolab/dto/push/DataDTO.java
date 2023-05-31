/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto.push;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DataDTO {

    private String viewName;
    private String jobName;
    private String jobUrl;
    private Long jobId;
    private long buildNumber;
    private String buildUrl;
    private String buildStatus;
    private List<ArtifactDTO> artifacts;

    private long timestamp;
    private String triggerName;

    private String clientId;

    public String toJsonString() {
        StringBuffer representation = new StringBuffer();
        representation.append("{viewName: \"");
        representation.append(viewName);
        representation.append("\", jobName: \"");
        representation.append(jobName);
        representation.append("\", jobUrl: \"");
        representation.append(jobUrl);
        representation.append("\"");
        representation.append(", jobId: ");
        representation.append(jobId);
        representation.append(", buildNumber: ");
        representation.append(buildNumber);
        representation.append(", buildUrl: \"");
        representation.append(buildUrl);
        representation.append("\", buildStatus: \"");
        representation.append(buildStatus);
        representation.append("\"");
        if (artifacts != null) {
            representation.append("[");
            for (ArtifactDTO artifact : artifacts) {
                representation.append(artifact.toJsonString());
                representation.append(",");
            }
            representation.append("]");
        }

        representation.append("}");
        return representation.toString();
    }
}
