package com.clarolab.dto;

import lombok.Data;

@Data
public class LogCommitsPerPersonAndPerDayDTO extends BaseDTO {

    private long authorId;
    private String authorName;
    private Long commitDate;
    private long commitCount;
    private String commitDay;

    public LogCommitsPerPersonAndPerDayDTO(long authorId, String authorName, Long commitDate, long commitCount, String commitDay) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.commitDate = commitDate;
        this.commitCount = commitCount;
        this.commitDay = commitDay;
    }
}
