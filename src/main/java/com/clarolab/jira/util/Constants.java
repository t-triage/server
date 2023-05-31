package com.clarolab.jira.util;

public class Constants {

    /** creo que no se necesitan los User Roles aca

    /**
     * Base Path
     */

    public static final String API_PATH = "/v1";

    /**
     * Single constants
     */

    public static final String CONFIGURATION = "/configuration";
    public static final String API_CONFIGURATION_URI = API_PATH + CONFIGURATION;

    /**
     * Jira Constants
     */
    public static final String API_JIRA = API_PATH + "/jiraApi";
    public static final String JIRA_API_PATH_URI = "/rest/api/3/issue/";
    public static final String JIRA_API_SEARCH_PROJECTS = "/rest/api/3/project/search";
    public static final String JIRA_PRE_API_DOMAIN= "https://api.atlassian.com/ex/jira/";
    public static final String JIRA_CLOUD_PATH_URI = "https://auth.atlassian.com/";
    public static final String CLIENT_ID = "pYyOful0cLBFRxvulu1hLRsCd14oAEDx";
    public static final String CLIENT_SECRET = "Ts2ZV2Sw_xl8KDngm4QeXcR6csv99dFpPAinKetiRUFux06uvXW2VJDKObm_j2DJ";
    public static final String REDIRECT_URL = "/jiraCallBack/Auth";
    //http%3A%2F%2Flocalhost%3A8088%2Fv1%2Finfo%2FjiraCallback
    public static final String JIRA_AUTH_URL = "https://auth.atlassian.com/oauth/token";

    public static final String API_JIRA_CODE = API_PATH + "/jiraCode";
    public static final String PROJECT_KEYS = "/projectKeys";
    public static final String JIRA_RESOURCES_URL = "https://api.atlassian.com/oauth/token/accessible-resources";
    public static final String JIRA_BOARD_FIRST = "https://api.atlassian.com/ex/jira";
    public static final String JIRA_BOARD_SECOND = "/rest/api/3/issue/createmeta";
    public static final String PROJECT_STATES = "projectStates";
    public static final String JIRA_API_PROJECT_STATUS = "/rest/api/3/statuses?";
    public static final String JIRA_API_PROJECTS = "/rest/api/3/project/";

    public static final String URL_FRONT_JIRACODE_SUCCESS = API_JIRA_CODE + "/success";

    public static final String API_JIRA_CODE_AUTH = API_JIRA_CODE + "/Auth";
    public static final String API_JIRA_CODE_ERROR = API_JIRA_CODE + "/error";

    public static final String BROWSE_ISSUE = "/browse/";
    public static final String SEARCH_ISSUETYPE = "/rest/api/3/issuetype/project";

}
