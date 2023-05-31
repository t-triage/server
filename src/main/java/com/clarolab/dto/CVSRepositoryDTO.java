package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVSRepositoryDTO extends BaseDTO{

    private String url;
    private String localPath;
    private String user;
    private String password;
    private String branch;
    private long lastRead;
    private String packageNames;
    private long product;

}
