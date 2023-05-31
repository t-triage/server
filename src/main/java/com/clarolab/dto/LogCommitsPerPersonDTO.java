package com.clarolab.dto;

import lombok.Data;

@Data
public class LogCommitsPerPersonDTO extends BaseDTO {

    private long authorId;
    private String authorName;
    private long commitCount;

    public LogCommitsPerPersonDTO(long authorId, String authorName, long commitCount){
        this.authorId=authorId;
        this.authorName=authorName;
        this.commitCount=commitCount;
    }



}
