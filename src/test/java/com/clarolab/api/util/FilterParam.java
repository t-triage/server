package com.clarolab.api.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterParam {

    @Builder.Default
    private boolean assignee = false;
    @Builder.Default
    private boolean pin = false;
    @Builder.Default
    private String executorName = null;
    @Builder.Default
    private boolean passingIssues = true;

    private Long containerId;
    @Builder.Default
    private boolean failures = false;
    @Builder.Default
    private boolean hideDisabled = false;
    @Builder.Default
    private String search = "";

    public String toAutomatedTestIssueJsonString() {
        String base = "{\"assignee\":%s,\"pin\":%s,\"executorName\":%s,\"passingIssues\":%s}";
        return String.format(base, assignee, pin, executorName, passingIssues);
    }

    public String toExecutorViewIssueJsonString() {
        String base = "{\"assignee\":%s, \"containerId\":%d, \"failures\":%s, \"hideDisabled\":%s, \"search\":\"%s\"}";
        return String.format(base, assignee, containerId, failures, hideDisabled, search);
    }

}
