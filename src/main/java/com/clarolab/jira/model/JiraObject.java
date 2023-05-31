package com.clarolab.jira.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JiraObject {

    private String access_token;
    private String scope;
    private long expires_in;
    private String token_type;
    private String refresh_token;
    private String id;
    private String key;
    private String self;
    private boolean error;

    @Builder
    private JiraObject(String access_token, String scope, long expires_in, String token_type,boolean error){
        this.access_token=access_token;
        this.scope=scope;
        this.expires_in=expires_in;
        this.token_type=token_type;
        this.error = error;
    }
}
