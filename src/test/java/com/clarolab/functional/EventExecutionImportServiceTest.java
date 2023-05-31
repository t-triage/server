package com.clarolab.functional;

import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.service.EventExecutionImportService;
import com.clarolab.populate.UseCaseDataProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Log
public class EventExecutionImportServiceTest extends BaseFunctionalTest {

    protected static String CSV_RESOURCES_PATH = "error_logs/";

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private EventExecutionImportService eventExecutionImportService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void emptyFile() {
        String packageNames = "com.clarolab";
        String layout = "%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable";

        List<EventExecution> events = eventExecutionImportService.read("", packageNames, layout);

        Assert.assertTrue(events.isEmpty());
    }

    @Test
    public void emptyPackageNames() {
        String logFile = getFileContent("logfile.log");
        String layout = "%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable";

        List<EventExecution> events = eventExecutionImportService.read(logFile, "", layout);

        Assert.assertTrue(events.isEmpty());
    }

    @Test
    public void emptyPattern() {
        String logFile = getFileContent("logfile.log");
        Assert.assertFalse(logFile.isEmpty());

        String packageNames = "com.clarolab";

        List<EventExecution> events = eventExecutionImportService.read(logFile, packageNames, "");

        Assert.assertTrue(events.isEmpty());
    }

    @Test
    public void invalidPattern() {
        String logFile = getFileContent("logfile.log");
        Assert.assertFalse(logFile.isEmpty());

        String packageNames = "com.clarolab";
        String layout = "%dd %pp %CC{1.} [%tt] %mm%nn";

        List<EventExecution> events = eventExecutionImportService.read(logFile, packageNames, layout);

        Assert.assertTrue(events.isEmpty());
    }

    @Test
    public void singleErrorParsing() {
        String logFile = getFileContent("single_error.log");
        Assert.assertFalse(logFile.isEmpty());

        String packageNames = "com.clarolab";
        String layout = "%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable";

        List<EventExecution> events = eventExecutionImportService.read(logFile, packageNames, layout);

        Assert.assertFalse(events.isEmpty());
        Assert.assertEquals(events.size(), 1);
    }

    @Test
    public void logFileParsing() {
        String logFile = getFileContent("logfile.log");
        Assert.assertFalse(logFile.isEmpty());

        String packageNames = "com.clarolab";
        String layout = "%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%logger{1.}): %msg%n%throwable";

        List<EventExecution> events = eventExecutionImportService.read(logFile, packageNames, layout);

        Assert.assertFalse(events.isEmpty());
    }

    @Test
    public void stdLogFileParsing() {
        String logFile = getFileContent("std-logger.log");
        Assert.assertFalse(logFile.isEmpty());

        String packageNames = "com.clarolab";
        String layout = "%d %p %C{1.} [%t] %m%n";

        List<EventExecution> events = eventExecutionImportService.read(logFile, packageNames, layout);

        Assert.assertFalse(events.isEmpty());
    }

    @Test
    public void importFromSplunk() {
        String logFile = getFileContent("json/splunk_event.json");

        HashMap<String, String> eventsMap = new Gson().fromJson(logFile, new TypeToken<HashMap<String, Object>>() {}.getType());

        SearchExecutor searchExecutor = SearchExecutor.builder()
                .name("test")
                .url("localhost:8089")
                .pattern("%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable")
                .packageNames("com.clarolab")
                .build();

        LogAlert alert = LogAlert.builder()
                .searchExecutor(searchExecutor)
                .build();

        List<EventExecution> events = eventExecutionImportService.readFromSplunk(Collections.singletonList(eventsMap), alert);

        Assert.assertFalse(events.isEmpty());
        Assert.assertEquals(events.size(), 1);
    }

    @Test
    public void importFromSplunkCSV() {
        String logFile = getFileContent("csv/splunk_events_push.csv");

        SearchExecutor searchExecutor = SearchExecutor.builder()
                .name("test")
                .url("localhost:8089")
                .pattern("%d %p %C{1.} [%t] %m%n")
                .packageNames("com.clarolab")
                .build();

        LogAlert alert = LogAlert.builder()
                .searchExecutor(searchExecutor)
                .build();

        List<EventExecution> events = eventExecutionImportService.readFromCVS(logFile, alert);

        Assert.assertFalse(events.isEmpty());
        Assert.assertEquals(26, events.size());
    }

    private String getFileContent(String filename) {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(CSV_RESOURCES_PATH + filename);
        String fileContent = "";
        try {
            fileContent = fileStream != null ? IOUtils.toString(fileStream, Charset.defaultCharset()) : null;
        } catch (IOException e) {
            Assert.fail("Couldn't read file: " + filename);
        }

        return fileContent;
    }

}