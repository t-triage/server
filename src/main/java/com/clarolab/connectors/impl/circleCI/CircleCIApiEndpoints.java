/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI;

public class CircleCIApiEndpoints {

    public static final String API_VERSION = "/api/v1.1";
    public static final String CIRCLE_BASE_URL = "https://circleci.com";
    public static final String CIRCLE_API_BASE_ENDPOINT = CIRCLE_BASE_URL + API_VERSION;


    public static final String ME_ENDPOINT = "/me";
    public static final String PROJECTS_ENDPOINT = "/projects";
    //Format:: /project/:vcs_type/:vcs_userName/:projectName?limit=:amountToReturn&offset=fromBuild
    public static final String BUILDS_FOR_PROJECT_ENDPOINT = "/project/%s/%s/%s";
    public static final String BUILDS_FOR_PROJECT_ENDPOINT_WITH_RANGE = BUILDS_FOR_PROJECT_ENDPOINT + "?offset=%d&limit=%d";
    public static final String BUILD_FOR_PROJECTS_ENDPOINT = "/project/%s/%s/%s";
    //Format:: /project/:vcs_type/:vcs_userName/:projectName/:build_num/artifacts
    public static final String BUILD_ARTIFACT = BUILDS_FOR_PROJECT_ENDPOINT+"/%d/artifacts";



    //Format:: /:vcs_type/:vcs_userName/:projectName
    //Example:: /bb/TTriage/qa-reports for Bitbucket or /gh/TTriage/qa-reports for github
    public static final String PROJECT_URL = CIRCLE_BASE_URL + "/%s/%s/%s";

}
