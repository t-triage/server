package com.clarolab.dto;

import lombok.Data;

@Data
public class LogCommitsPerDayDTO extends BaseDTO {

    private Long commitDate;
    private String commitDay;
    private long commitCount;

    public LogCommitsPerDayDTO(Long commitDate, long commitCount, String commitDay) {
        this.commitCount = commitCount;
        this.commitDate = commitDate;
        this.commitDay = commitDay;
    }

}
