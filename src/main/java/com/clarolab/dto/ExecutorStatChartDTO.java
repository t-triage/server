package com.clarolab.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ExecutorStatChartDTO extends BaseDTO {
    private long executor;

    private long expected;
    private long required;

    private long actual;

    private String date;

    @Builder
    public ExecutorStatChartDTO(long executor, long expected, long required, long actual, String date) {
        this.executor = executor;
        this.expected = expected;
        this.required = required;
        this.actual = actual;
        this.date = date;
    }

}