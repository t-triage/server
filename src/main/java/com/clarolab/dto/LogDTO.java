package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO extends BaseDTO{

    private String author;
    private String approver;
    private String commitHash;
    private long date;
    private String test;
    private long testId;
    private long authorId;
}
