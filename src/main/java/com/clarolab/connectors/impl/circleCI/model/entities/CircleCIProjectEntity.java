/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import com.clarolab.connectors.impl.circleCI.CircleCIApiEndpoints;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "circleCIProjectEntityBuilder", buildMethodName = "circleCIProjectEntityBuild")
@AllArgsConstructor
@Data
public class CircleCIProjectEntity {

    private String reponame;
    private String username;
    private String vcs_type;
    private String vcs_url;

    @JsonIgnore
    private static final String SEPARATOR = "/";

    public String getDescription(){
        return vcs_type + SEPARATOR + username + SEPARATOR + reponame;
    }

    public String getUrl(){
        return String.format(CircleCIApiEndpoints.PROJECT_URL, getProjectVcsType(), username, reponame);
    }

    public String getProjectVcsType(){
        return  this.vcs_type.equals("bitbucket") ? "bb" : "gh";
    }

}
