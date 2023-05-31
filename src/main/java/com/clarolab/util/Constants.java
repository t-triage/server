/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */
/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import org.aspectj.bridge.MessageUtil;

public final class Constants {

    /**
     * User Roles used in Controllers
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_SERVICE = "ROLE_SERVICE";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String PROVIDER_INTERNAL = "PROVIDER_INTERNAL";
    public static final String PROVIDER_GOOGLE = "PROVIDER_GOOGLE";
    public static final String PROVIDER_ONELOGIN = "PROVIDER_ONELOGIN";
    public static final String PROVIDER_OKTA = "PROVIDER_OKTA";

    public static final String SPACE = " ";
    public static final String COLON = ",";
    public static final String SEMI_COLON = ";";

    //it defines the min amount of chars to hit the search
    public static final int MIN_SEARCH_LENGHT_ = 2;
    public static final int DEFAULT_MINIMUN_REAL_NAME_LENGTH = 5;

    public static final Long longTimeOut = 7000L;

    public static final String DEADLINE_FREQUENCY_EVERY_FRIDAY = "0 0 17 * FRI"; //Every Friday at 17:00
    public static final String DEADLINE_FREQUENCY_LAST_DAY = "0 59 23 * * *";
    public static final String DEADLINE_FREQUENCY_EVERY_DAY = "0 30 23 * * *";
    public static final String DEADLINE_FREQUENCY_EVERY_WEEKDAY = "0 0 17 * * MON,TUE,WED,THU,FRI";
    public static final String DEADLINE_FREQUENCY_2DAYS = "0 0 17 * * TUE,THU";
    public static final String DEADLINE_FREQUENCY_3DAYS = "0 0 17 * * MON,WED,FRI";

    /**
     * Base Path
     */

    public static final String ROOT_PATH = "/";
    public static final String API_PATH = "/v1";

    /**
     * Single constants
     */

    public static final String ID = "/{id}";
    public static final String NAME = "/{name}";
    public static final String TEST = "/test";
    public static final String POPULATE = "/populate";
    public static final String VALIDATE = "/validate";
    public static final String BY_NAME = "/name";
    public static final String NAMES = "/names";
    public static final String BY_ID = "/id";
    public static final String BY_EXTERNAL_ID = "/externalId";
    public static final String EXECUTOR = "/executor";
    public static final String TREND_GOAL = "/trendGoal";
    public static final String PRODUCT_GOAL = "/productGoal";
    public static final String FUNCTIONALITY = "/functionality";
    public static final String CONTAINER = "/container";
    public static final String BUILD = "/build";
    public static final String NOTE = "/note";
    public static final String NOTIFICATION = "/notification";
    public static final String DEADLINE = "/deadline";
    public static final String ISSUETICKET = "/issueticket";
    public static final String PRODUCT = "/product";
    public static final String CVSREPOSITORY = "/cvsRepository";
    public static final String SPEC = "/spec";
    public static final String USER = "/user";
    public static final String DELETE = "/delete";
    public static final String PRIORITY = "/priority";
    public static final String ONGOING = "/ongoing";
    public static final String ACTIONS = "/actions";
    public static final String AUTOMATED_TEST_ISSUE = "/automatedtestIssue";
    public static final String AUTOMATED_TEST = "/automatedtest";
    public static final String SLACK_INTEGRATION = "/slack";
    public static final String SEARCH = "/search";
    public static final String STEPS = "/steps";
    public static final String TERMS = "/terms";
    public static final String PIN = "/pin";
    public static final String DETAIL = "/detail";
    public static final String DRAFT = "/draft";
    public static final String TRIAGED = "/triaged";
    public static final String TRIAGED_ALL = "/triagedAll";
    public static final String ASSIGN = "/assign";
    public static final String INVALIDATE = "/invalidate";
    public static final String APPROVE = "/approve";
    public static final String PROCESS = "/process";
    public static final String ME = "/@me";
    public static final String FILTERS = "/filters";
    public static final String PUSH_PATH = "/push";
    public static final String USER_IP = "TTRIAGE-IP";
    public static final String USER_USERNAME = "TTRIAGE-USERNAME";
    public static final String CONTAINERS = "/containers";
    public static final String HISTORY = "/history";
    public static final String AUTH = "/auth";
    public static final String LOGIN = "/login";
    public static final String TOKEN = "/token";
    public static final String SIGNUP = "/signup";
    public static final String SEARCH_FUNCTIONALITY = "/functionality/search";
    public static final String ASSIGN_TO_PLAN = "/Assign";
    public static final String SEARCH_AUTOMATION = "/toAutomate";
    public static final String IMPORT_REPORT = "/importReport";
    public static final String IMPORT_EVENTS_LOG = "/importLogEvents";
    public static final String DELETE_BY_PLAN_AND_CASE = "/deleteByPlanAndCase";
    public static final String ONBOARDING = "/onboarding";
    public static final String ITEMS = "/items";
    public static final String PIPELINE = "/pipeline";
    public static final String HELP = "/help";
    public static final String CSV = "/csv";
    public static final String CHECK = "/check";
    public static final String STATUS = "/status";
    public static final String COUNT = "/count";
    public static final String COMMENT = "/comment";
    public static final String TRANSITIONS = "/transitions";
    public static final String MARK_AS_SEEN = "/markAsSeen";
    public static final String LOG_ALERT = "/logAlert";
    public static final String LOG_CONSOLE = "/log";
    public static final String EVENT_EXECUTION = "/eventExecution";
    public static final String ERROR_CASE = "/errorCase";
    public static final String SEARCH_EXECUTOR = "/searchExecutor";
    public static final String LIST_BY_ERROR_CASE = "/listByErrorCase";
    public static final String DELETE_BY_AUTOMATED_COMPONENT_AND_TEST = "/deleteByComponentAndTest";
    public static final String ADD_COMMENT = "/addComment";
    public static final String TRANSITION_ISSUE = "/transitionIssue";
    public static final String CREATE_NEW_ISSUE = "/createJiraObject";

    public static final String VIEW = "/view";
    public static final String VIEW_PATH = API_PATH + VIEW;
    public static final String TRIAGE_PATH = API_PATH + "/triage";
    public static final String LOG_CONSOLE_PATH = API_PATH + LOG_CONSOLE;
    public static String LOG_CONSOLE_FILEPATH = "logs/std-logger.log";
    public static final String API_TESTCASE_URI = API_PATH + TEST;
    public static final String API_BUILD_URI = API_PATH + BUILD;
    public static final String API_NOTE_URI = API_PATH + NOTE;
    public static final String API_NOTIFICATION_URI = API_PATH + NOTIFICATION;
    public static final String API_EXECUTOR_STAT_URI = API_PATH + "/executorStat";
    public static final String API_TRAIGESPECY_URI = API_PATH + SPEC;
    public static final String API_MANUAL_TEST_URI = API_PATH + "/manualTest";
    public static final String API_MANUAL_PLAN = "/manualPlan";
    public static final String API_MANUAL_PLAN_URI = API_PATH + API_MANUAL_PLAN;
    public static final String API_MANUAL_EXECUTION_URI = API_PATH + "/manualExecution";
    public static final String API_PRODUCT_COMPONENT_URI = API_PATH + "/productComponent";
    public static final String API_BOARD_URI = API_PATH + "/board";
    public static final String API_PIPELINE = API_PATH + PIPELINE;
    public static final String DELETE_BY_PIPELINE_AND_CASE = "/deleteWithTest";
    public static final String FIND_PIPELINES_BY_CONTAINER = "/container";
    public static final String API_AUTOMATED_COMPONENT_URI = API_PATH + "/automatedComponent";

    public static final String API_USER_URI = API_PATH + USER;
    public static final String API_SLACK_URI = API_PATH + SLACK_INTEGRATION;
    public static final String API_USER_PREFERENCES_URI = API_USER_URI + "/preference";
    public static final String API_REPORT_URI = API_PATH + "/report";
    public static final String API_REPORT_BUILD_URI = API_REPORT_URI + BUILD;
    public static final String API_LICENSE_URI = API_PATH + "/license";
    public static final String API_LOG_ALERT = API_PATH + LOG_ALERT;
    public static final String API_EVENT_EXECUTION = API_PATH + EVENT_EXECUTION;
    public static final String API_ERROR_CASE = API_PATH + ERROR_CASE;
    public static final String API_SEARCH_EXECUTOR = API_PATH + SEARCH_EXECUTOR;

    public static final String API_EXECUTOR_URI = API_PATH + EXECUTOR;
    public static final String API_TREND_GOAL_URI = API_PATH + TREND_GOAL;
    public static final String API_PRODUCT_GOAL_URI = API_PATH + PRODUCT_GOAL;
    public static final String API_ONBOARDING_URI = API_PATH + ONBOARDING;
    public static final String API_PROPERTY_URI = API_PATH + "/property";
    public static final String API_CONNECTOR_URI = API_PATH + "/connector";
    public static final String API_CONTAINER_URI = API_PATH + CONTAINER;
    public static final String API_DEADLINE_URI = API_PATH + DEADLINE;
    public static final String API_INFO_URI = API_PATH + "/info";
    public static final String API_ISSUETICKET_URI = API_PATH + ISSUETICKET;
    public static final String API_PRODUCT_URI = API_PATH + PRODUCT;
    public static final String API_CVSREPOSITORY_URI = API_PATH + CVSREPOSITORY;
    public static final String API_STATS_URI = API_PATH + "/stats";
    public static final String API_AUTOMATED_TEST_URI = API_PATH + AUTOMATED_TEST_ISSUE;
    public static final String API_AUTOMATED_TEST_CASE_URI = API_PATH + AUTOMATED_TEST;
    public static final String API_AUTOMATED_TEST_PENDING = "/pendingfix";
    public static final String API_FEATURE_URI = "/feature";
    public static final String API_RELASE_STATUS_URI = "/status";

    public static final String API_FUNCTIONALITY_URI = API_PATH + FUNCTIONALITY;
    public static final String FIND_FUNCTIONALITY_BY_EXTERNAL_ID = "/find" + BY_EXTERNAL_ID;

    public static final String TEST_DATA_PATH = "test_data/";
    public static final String TEST_SQL_PATH = "test_sql/";

    /**
     * Actions
     **/
    public static final String API_ACTIONS_URI = API_PATH + ACTIONS;
    public static final String ACTION_ASSIGN_JOB = BUILD + ASSIGN;
    public static final String ACTION_ASSIGN_TEST = TEST + ASSIGN;
    public static final String ACTION_TRIAGED_JOB = BUILD + TRIAGED;
    public static final String ACTION_TRIAGED_TEST = TEST + TRIAGED;
    public static final String ACTION_DRAFT_TEST = TEST + DRAFT;
    public static final String ACTION_INVALIDATE_JOB = BUILD + INVALIDATE;
    public static final String ACTION_DISABLE_JOB = BUILD + "/disable";
    public static final String ACTION_ENABLE_JOB = BUILD + "/enable";
    public static final String ACTION_APPROVE_JOB = BUILD + APPROVE;
    public static final String ACTION_ASSIGN_AUTOMATION_ISSUE = AUTOMATED_TEST_ISSUE + ASSIGN;


    /**
     * Reports Views
     */
    public static final String API_BUILDREPORT_URI = TRIAGE_PATH + BUILD;
    public static final String API_TESTCASEEPORT_URI = TRIAGE_PATH + TEST;
    public static final String API_VIEW_EXECUTORS_URI = VIEW_PATH + EXECUTOR;

    public static final String SUGGESTED_URI = "/suggested";
    public static final String API_EXPORT_URI = API_PATH + "/export";
    public static final String USER_FULL_REPORT = "/userReport";
    public static final String PRODUCT_FULL_REPORT = "/productReport";

    public static final String EXPORT_PRODUCTREPORT = "PRODUCTREPORT";
    public static final String EXPORT_USERREPORT = "USERREPORT";
    public static final String EXPORT_EXECUTORLIST = "EXECUTORLIST";
    public static final String EXPORT_USERLIST = "USERLIST";


    /**
     * CRUD Paths
     */
    public static final String CREATE_PATH = "/create";
    public static final String GET = "/get";
    public static final String READ_PATH = GET + ID;
    public static final String UPDATE_PATH = "/update";
    public static final String DELETE_PATH = DELETE + ID;

    public static final String LIST_PATH = "/list";
    public static final String PAGE_PATH = "/page";
    public static final String FIND_PATH = "/find" + NAME;
    public static final String FIND_ID_PATH = "/find" + ID;
    public static final String GET_NAME_PATH = NAME;

    /**
     * Actions
     */
    public static final String RUN_PATH = "/retrieve" + ID;


    public static final String POPULATE_PATH = POPULATE;
    public static final String POPULATE_BY_ID_PATH = POPULATE_PATH + BY_ID + ID;
    public static final String VALIDATE_BY_ID_PATH = VALIDATE + ID;
    public static final String POPULATE_BY_NAME_PATH = POPULATE + BY_NAME + NAME;
    public static final String TESTS_FROM_EXECUTOR = ID + TEST;
    public static final String INTERNAL_PATH = "/internaluser";
    public static final String NEW_TOKEN_PATH = AUTH + CREATE_PATH + ID;
    public static final String GET_TOKEN_PATH = AUTH + GET + ID;

    /**
     * Stats
     */
    public static final String STATS_JOB_EXECUTION = "/jobExecution";
    public static final String STATS_TOTAL_BUILD_FAILS = "/buildFails";
    public static final String STATS_HISTORIC_TRIAGED = "/historicTriages";
    public static final String STATS_SAVED_TIME = "/totalSavedTime";
    public static final String STATS_DEADLINES_COMPLETED = "/deadlinesCompleted";
    public static final String STATS_GLOBAL_BURNDOWN = "/globalBurndownFailNewFixes";
    public static final String STATS_EXECUTORS_TO_TRIAGE = "/toTriage";
    public static final String STATS_COMPONENT_BASED_TRIAGE = "/componentBasedTriages";
    public static final String STATS_BUG_FILED = "/bugFiled";
    public static final String STATS_MISSING_DEADLINES = "/missingDeadlines";
    public static final String STATS_TESTS_SUMMARY = "/testSummary";
    public static final String STATS_PRODUCTS_SUMMARY = "/productSummary";
    public static final String STATS_UNIQUE_TESTS = "/uniqueTests";
    public static final String STATS_FAIL_EXCEPTIONS = "/failExceptions";
    public static final String STATS_TOTAL_TRIAGED_FAILS = "/totalTriagedFails";
    public static final String STATS_TOTAL_AUTOMATION_FIXES = "/totalAutomationFixes";
    public static final String STATS_TOTAL_AUTOMATION_PENDING_AND_FIXES_FOR_USER = "/totalAutomationPendingAndFixes/loggedUser";
    public static final String STATS_TOTAL_TRIAGES_FOR_DAY_FOR_USER = "/totalTriagesForDay/loggedUser";
    public static final String STATS_ENGINEER_EFFORT = "/engineerEffort";
    public static final String STATS_PRODUCTIVITY = "/productivity";
    public static final String STATS_SUITE_EVOLUTION = "/suiteEvolution";
    public static final String STATS_FAILED_TESTS = "/failedTests";
    public static final String STATS_TOTAL_AUTOMATION_FIXES_USERS = "/totalAutomationFixes" + USER;
    public static final String STATS_TOTAL_AUTOMATION_PENDING_USERS = "/totalAutomationPending" + USER;
    public static final String STATS_TOTAL_AUTOMATION_FIXED_PENDING_USERS = "/totalAutomationFixedAndPending" + USER;
    public static final String STATS_TOTAL_COMMITS_PER_USER = "/totalCommitsPerUser";
    public static final String STATS_TOTAL_COMMITS_PER_DAY = "/totalCommitsPerDay";
    public static final String STATS_TOTAL_COMMITS_PER_PERSON_AND_PER_DAY = "/totalCommitsPerPersonAndPerDay";
    public static final String STATS_TOTAL_COMMITS = "/totalCommits";

    public static final String STATS_TOTAL_TEST_ERRORS = "/manualTest";

    public static final String MANUALTESTCASE_SINCE = "/manualTestSince";

    public static final String MANUALTESTEXECUTION_SINCE = "/manualTestExecutionSince";
    public static final int DEFAULT_DELAY = 1;
    public static final int DEFAULT_SCHEDULER_POOL_SIZE = 4;
    public static final int DEFAULT_DATABASE_POOL = 7;
    public static final int DEFAULT_DATABASE_MAX_TIMELINE = 1000 * 60 * 10; //Ten Min
    public static final long DEFAULT_EVENT_PROCESS_DELAY = 1000L; //X Seconds
    public static final long DEFAULT_AGENT_DELAY = DEFAULT_DELAY * 1000 * 60 * 2L; //X Seconds
    public static final long DEFAULT_AGENT_TRIAGE_JOB_TIMEOUT = DEFAULT_DELAY * 1000 * 60 * 11L; //Every 15 min
    public static final long DEFAULT_AGENT_STATS_JOB_TIMEOUT = DEFAULT_DELAY * 1000 * 60 * 15L; //Every 15 min
    public static final String DEFAULT_AGENT_PRODUCTIVITY_JOB_TIMEOUT = "1 2 0 ? * *";
    public static final long DEFAULT_AGENT_TIME_TIMEOUT = DEFAULT_DELAY * 1000 * 60 * 59 * 2L; //Every 2 hs
    public static final long DEFAULT_CONNECTORS_JOB_TIME = 1000 * 60 * 10L;
    public static final String DEFAULT_CONNECTORS_JOB_CRON = "0 0 17 * * SAT"; //Every Saturday at 17:00
    public static final String START_DAY_JOB_CRON = "0 5 22/10 ? * * "; // expressed in utc, it is ours 1 am and 13hs
    public static final long DEFAULT_POPULATE_FREQUENCY = DEFAULT_DELAY * 1000 * 60 * 13L; //Every 1h
    public static final long DEFAULT_POPULATE_DELAY = DEFAULT_DELAY * 1000 * 20; //X Seconds

    /**
     * FRONTEND URLS
     */
    public static final String URL_FRONT_EXECUTOR = "/SuiteList/%d/Kanban";
    public static final String URL_FRONT_CONTAINER = "/SuiteList/Container/%d";
    public static final String URL_FRONT_USER = USER + "/%d";


    public static final int UI_SUGGESTION_SIZE = 5;
    public static final int UI_GRAPH_BUILD_SIZE = 80;
    public static final int AUTOMATION_TREND_SIZE = 10;

    /**
     * DEFAULTS VALUES FOR PROPERTIES
     */
    public static final int DEFAULT_DAYS_TO_EXPIRE_TRIAGE = 4;
    public static final int DEFAULT_MAX_BUILDS_TO_PROCESS = 1; //Number of builds that will be stored on db
    public static final boolean DEBUG_STEP_PROCESS = false; //If it imports one by one
    public static final int DEFAULT_MAX_BUILDS_TO_RETRIEVE = 10; //Number of builds that will be recovered from CI tool to be limited by @DEFAULT_MAX_BUILDS_TO_PROCESS
    public static final int DEFAULT_MAX_TESTCASES_TO_PROCESS = 20; //Number of previous test cases to compare
    public static final int DEFAULT_MAX_FLAKY_CHANGES = 3; //Number of times a test switches from fail to pass before being considered flaky.
    public static final int DEFAULT_MAX_TESTCASES_PER_DAY = 50; //Number of tests cases that are triaged per day per suite
    public static final int DEFAULT_CONSECUTIVE_PASS_COUNT = 10;
    public static final int DEFAULT_PREVIOUS_FAIL_COUNT = 5;
    public static final int DEFAULT_MAX_EVENTS_TO_PROCESS = 20;
    public static final int DEFAULT_OLD_EVENTS_TO_DELETE_DAYS = -30 * 2;
    public static final int DEFAULT_SAVED_TIME_MIN = 10;
    public static final int DEFAULT_SAVED_TIME_PERIOD = 60;
    public static final boolean DEFAULT_SLACK_ENABLED = true && DEFAULT_DELAY == 1;
    public static final boolean DEFAULT_GDPR_ENABLED = true;
    public static final int DEFAULT_PREVIOUS_DAYS_VALID_INFO = 7;
    public static final boolean DEFAULT_INTERNAL_LOGIN_ENABLED = true;
    public static final boolean DEFAULT_SERVICE_LOGIN_ENABLED = true;
    public static final boolean DEFAULT_AUTOTRIAGE_SAME_ERROR_TEST_ENABLED = true;
    public static final long DEFAULT_JWT_AUTH_TOKEN_EXPIRATION_MS = 864000000L; //10 Days
    public static final int DEFAULT_HTTP_MAX_RETRY = 5;
    public static final String DEFAULT_TEMP_DIR = "/tmp";
    public static final String DEFAULT_TandC_FILE = "texts/terms.txt";
    public static final String DEFAULT_TriageHelp_FILE = "texts/triage.html";
    public static final int DEFAULT_STATS_FAIL_EXECPTION_TOP_LIMIT = 5;
    public static final int DEFAULT_STACK_TRACE_EXCEPTIONS_TO_PROCESS = 5;
    public static final boolean DEFAULT_AGENT_PRODUCTIVITY_SERVICE_ENABLED = true;
    public static final String DEFAULT_GOOGLE_ANALYTICS_UA = "UA-145097787-2";
    public static final boolean DEFAULT_RULE_ENGINE_ON = true;
    public static final long DEFAULT_TERMS_AND_CONDITION_ACCEPTED_TIME = 0;
    public static final int DEFAULT_MAX_ITEMS_PER_PAGE = 200;
    public static final boolean DEFAULT_FEATURE_MANUAL_TEST_ENABLED = true;
    public static final int DEFAULT_FREE_LICENSE_MAX_USERS = 5;
    public static final int DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE = 200;
    public static final int DEFAULT_FREE_LICENSE_MAX_MANUAL_TEST_CASES = 50;

    public static final int JIRA_TOKEN_MONTH_NOTIFICATION = 2;

    // Property names
    public static final String WELCOME_MESSAGE = "WELCOME_MESSAGE";
    // public static final String GOOGLE_UA = "GOOGLE_ANALYTICS_UA";
    public static final String DAYS_TO_EXPIRE_TRIAGE = "DAYS_TO_EXPIRE_TRIAGE";
    public static final String MAX_TESTCASES_TO_PROCESS = "MAX_TESTCASES_TO_PROCESS";
    public static final String MAX_TESTCASES_PER_DAY = "MAX_TESTCASES_PER_DAY";
    public static final String PREVIOUS_DAYS_VALID_INFO = "PREVIOUS_DAYS_VALID_INFO";
    public static final String SAVED_TIME_MIN = "SAVED_TIME_MIN";
    public static final String SAVED_TIME_PERIOD = "SAVED_TIME_PERIOD";
    public static final String CONSECUTIVE_PASS_COUNT = "CONSECUTIVE_PASS_COUNT";
    public static final String PREVIOUS_FAIL_COUNT = "PREVIOUS_FAIL_COUNT";
    public static final String MAX_EVENTS_TO_PROCESS = "MAX_EVENTS_TO_PROCESS";
    public static final String OLD_EVENTS_TO_DELETE_DAYS = "OLD_EVENTS_TO_DELETE_DAYS";
    public static final String SLACK_ENABLED = "SLACK_ENABLED";
    public static final String INTERNAL_LOGIN_ENABLED = "INTERNAL_LOGIN_ENABLED";
    public static final String SERVICEL_LOGIN_ENABLED = "SERVICEL_LOGIN_ENABLED";
    public static final String JWT_AUTH_TOKEN_EXPIRATION_MS = "JWT_AUTH_TOKEN_EXPIRATION_MS";
    public static final String AUTOTRIAGE_SAME_ERROR_TEST = "AUTOTRIAGE_SAME_ERROR_TEST";
    public static final String GDPR_ENABLED = "GDPR_ENABLED";
    public static final String TERM_AND_CONDITIONS = "TERM_AND_CONDITIONS";
    public static final String TRIAGE_HELP = "TRIAGE_HELP";
    public static final String STATS_FAIL_EXCEPTION_TOP_LIMIT = "STATS_FAIL_EXCEPTION_TOP_LIMIT";
    public static final String AGENT_PRODUCTIVITY_SERVICE_ENABLED = "AGENT_PRODUCTIVITY_SERVICE_ENABLED";
    public static final String STACK_TRACE_EXCEPTIONS_TO_PROCESS = "STACK_TRACE_EXCEPTIONS_TO_PROCESS";
    public static final String RULE_ENGINE_ON = "RULE_ENGINE_ON";
    public static final String GOOGLE_ANALYTICS_UA = "GOOGLE_ANALYTICS_UA";
    public static final String TERMS_AND_CONDITION_ACCEPTED_TIME = "TERMS_AND_CONDITION_ACCEPTED_TIME";
    public static final String FEATURE_MANUAL_TEST_ENABLED = "FEATURE_MANUAL_TEST_ENABLED";
    public static final String URL_FRONT = "URL";

    /**
     * Table Names
     */

    private static final String TABLE_PREFIX = "QA";
    private static final String TABLE_SEPARATOR = "_";
    public static final String TABLE_BUILD = TABLE_PREFIX + TABLE_SEPARATOR + "BUILD";
    public static final String TABLE_ARTIFACT = TABLE_PREFIX + TABLE_SEPARATOR + "ARTIFACT";
    public static final String TABLE_EVENT = TABLE_PREFIX + TABLE_SEPARATOR + "EVENT";
    public static final String TABLE_BUILD_TRIAGE = TABLE_PREFIX + TABLE_SEPARATOR + "BUILD_TRIAGE";
    public static final String TABLE_EXTERNAL_BUILD_TRIAGE = TABLE_PREFIX + TABLE_SEPARATOR + "EXTERNAL_BUILD_TRIAGE";
    public static final String TABLE_CONNECTOR = TABLE_PREFIX + TABLE_SEPARATOR + "CONNECTOR";
    public static final String TABLE_CONTAINER = TABLE_PREFIX + TABLE_SEPARATOR + "CONTAINER";
    public static final String TABLE_DEADLINE = TABLE_PREFIX + TABLE_SEPARATOR + "DEADLINE";
    public static final String TABLE_EXECUTOR = TABLE_PREFIX + TABLE_SEPARATOR + "EXECUTOR";
    public static final String TABLE_EVOLUTION_GOAL = TABLE_PREFIX + TABLE_SEPARATOR + "GOAL";
    public static final String TABLE_PRODUCT_GOAL = TABLE_PREFIX + TABLE_SEPARATOR + "PRODUCT_GOAL";
    public static final String TABLE_PIPELINE = TABLE_PREFIX + TABLE_SEPARATOR + "PIPELINE";
    public static final String TABLE_PIPELINE_TEST = TABLE_PREFIX + TABLE_SEPARATOR + "PIPELINE_TEST";
    public static final String TABLE_ISSUE_TICKET = TABLE_PREFIX + TABLE_SEPARATOR + "ISSUE_TICKET";
    public static final String TABLE_LICENSE = TABLE_PREFIX + TABLE_SEPARATOR + "LICENSE";
    public static final String TABLE_NOTE = TABLE_PREFIX + TABLE_SEPARATOR + "NOTE";
    public static final String TABLE_NOTIFICATION = TABLE_PREFIX + TABLE_SEPARATOR + "NOTIFICATION";
    public static final String TABLE_PRODUCT = TABLE_PREFIX + TABLE_SEPARATOR + "PRODUCT";
    public static final String TABLE_PROPERTY = TABLE_PREFIX + TABLE_SEPARATOR + "PROPERTY";
    public static final String TABLE_REPORT = TABLE_PREFIX + TABLE_SEPARATOR + "REPORT";
    public static final String TABLE_PRODUCTIVITY = TABLE_PREFIX + TABLE_SEPARATOR + "PRODUCTIVITY";
    public static final String TABLE_LOG = TABLE_PREFIX + TABLE_SEPARATOR + "REPOSITORY_LOG";
    public static final String TABLE_CVS_REPOSITORY = TABLE_PREFIX + TABLE_SEPARATOR + "CVS_REPOSITORY";
    public static final String TABLE_TEST_EXECUTION = TABLE_PREFIX + TABLE_SEPARATOR + "TEST_EXECUTION";
    public static final String TABLE_TEST_EXECUTION_STEP = TABLE_PREFIX + TABLE_SEPARATOR + "TEST_EXECUTION_STEP";
    public static final String TABLE_TEST_CASE = TABLE_PREFIX + TABLE_SEPARATOR + "TEST_CASE";
    public static final String TABLE_TEST_TRIAGE = TABLE_PREFIX + TABLE_SEPARATOR + "TEST_TRIAGE";
    public static final String TABLE_TRIAGE_SPEC = TABLE_PREFIX + TABLE_SEPARATOR + "TRIAGE_SPEC";
    public static final String TABLE_USER = TABLE_PREFIX + TABLE_SEPARATOR + "USER";
    public static final String TABLE_USER_PREFERENCE = TABLE_PREFIX + TABLE_SEPARATOR + "USER_PREFERENCE";
    public static final String TABLE_SLACK_SPEC = TABLE_PREFIX + TABLE_SEPARATOR + "SLACK_SPEC";
    public static final String TABLE_STAT_EXECUTOR = TABLE_PREFIX + TABLE_SEPARATOR + "EXECUTOR_STAT";
    public static final String TABLE_STAT_PRODUCT = TABLE_PREFIX + TABLE_SEPARATOR + "PRODUCT_STAT";
    public static final String TABLE_STAT_MANUAL_TEST = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_STAT";
    public static final String TABLE_STAT_USER = TABLE_PREFIX + TABLE_SEPARATOR + "USER_STAT";
    public static final String TABLE_AUTOMATED_TEST_ISSUE = TABLE_PREFIX + TABLE_SEPARATOR + "AUTOMATED_TEST_ISSUE";
    public static final String TABLE_APPLICATION_DOMAIN = TABLE_PREFIX + TABLE_SEPARATOR + "APPLICATION_DOMAIN";
    public static final String TABLE_PIN = TABLE_PREFIX + TABLE_SEPARATOR + "PIN";
    public static final String TABLE_IMAGE = TABLE_PREFIX + TABLE_SEPARATOR + "IMAGE";
    public static final String TABLE_ERROR_DETAIL = TABLE_PREFIX + TABLE_SEPARATOR + "ERROR_DETAIL";
    public static final String TABLE_SERVICE_AUTH = TABLE_PREFIX + TABLE_SEPARATOR + "SERVICE_AUTH";
    public static final String TABLE_NEWS_BOARD = TABLE_PREFIX + TABLE_SEPARATOR + "NEWS_BOARD";
    public static final String TABLE_GUIDE = TABLE_PREFIX + TABLE_SEPARATOR + "ONBOARD_GUIDE";
    public static final String TABLE_USER_REACTION = TABLE_PREFIX + TABLE_SEPARATOR + "ONBOARD_USER";
    public static final String TABLE_STAT_EVOLUTION = TABLE_PREFIX + TABLE_SEPARATOR + "EVOLUTION_STAT";
    public static final String TABLE_EVENT_EXECUTION = TABLE_PREFIX + TABLE_SEPARATOR + "EVENT_EXECUTION";
    public static final String TABLE_ERROR_CASE = TABLE_PREFIX + TABLE_SEPARATOR + "ERROR_CASE";
    public static final String TABLE_LOG_ALERT = TABLE_PREFIX + TABLE_SEPARATOR + "LOG_ALERT";
    public static final String TABLE_SEARCH_EXECUTOR = TABLE_PREFIX + TABLE_SEPARATOR + "SEARCH_EXECUTOR";
    public static final String TABLE_JIRA_CONFIGURATION = TABLE_PREFIX + TABLE_SEPARATOR + "JIRA_CONFIGURATION";


    public static final String TABLE_MANUAL_TEST_CASE = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_CASE";
    public static final String TABLE_MANUAL_TEST_EXECUTION = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_EXECUTION";
    public static final String TABLE_MANUAL_TEST_PLAN = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_PLAN";
    public static final String TABLE_MANUAL_TEST_STEP = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_STEP";
    public static final String TABLE_MANUAL_TEST_REQUIREMENT = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_REQUIREMENT";
    public static final String TABLE_MANUAL_TEST_TECHNIQUE = TABLE_PREFIX + TABLE_SEPARATOR + "MANUAL_TEST_TECHNIQUE";
    public static final String TABLE_PRODUCT_COMPONENT = TABLE_PREFIX + TABLE_SEPARATOR + "PRODUCT_COMPONENT";
    public static final String TABLE_AUTOMATED_COMPONENT = TABLE_PREFIX + TABLE_SEPARATOR + "AUTOMATED_COMPONENT";
    public static final String TABLE_TEST_COMPONENT_RELATION = TABLE_PREFIX + TABLE_SEPARATOR + "TEST_COMPONENT_RELATION";
    public static final String TABLE_MANUAL_TEST_FUNCTIONALITY = TABLE_PREFIX + TABLE_SEPARATOR + "FUNCTIONALITY";


    /**
     * CVSLog pointers
     */
    public static final String SECURITY_LOG = "security-logger";


    /**
     * Data images
     */
    public static final String DATA_IMAGE_PREFIX = "data:image/jpeg;base64,";
    public static final String DATA_IMAGE_TYPE = "jpg";
    public static final float DATA_IMAGE_QUALITY = 0.05f;

    /**
     * AutomatedTestIssue points priorities
     */
    public static final int INITIAL_PRIORITY = 10;
    public static final int HIGH_PRIORITY = 240;
    public static final int MEDIUM_PRIORITY = 90;
    public static final int PASSING_PERMANENT_PRIORITY = 15;
    public static final int FAIL_AGAIN_PRIORITY = 15;
    public static final int PASSING_TO_FIX_AMOUNT = 4;
    public static final int BLOCKER_PRIORITY = 390;

    /**
     * AutomatedComponents
     */
    public static final int MAX_AUTOMATED_COMPONENTS_ALLOWED = 6;

}
