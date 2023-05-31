package com.clarolab.logtriage.service;

import ch.qos.logback.core.pattern.parser.CompositeNode;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.pattern.parser.SimpleKeywordNode;
import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.util.parser.LogHeader;
import com.clarolab.logtriage.util.parser.format.*;
import com.clarolab.util.Pair;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Service
@Log
public class EventExecutionImportService {

    @Autowired
    private ErrorCaseService errorCaseService;

    @Autowired
    private EventExecutionService eventExecutionService;

    private static final String DEFAULT_SEPARATOR = " ";

    public List<EventExecution> readFromCVS(String csvContent, LogAlert alert) {
        List<EventExecution> events = new ArrayList<>();
        SearchExecutor search = alert.getSearchExecutor();

        try {
            List<LogFormat> logFormats = parsePattern(search.getPattern());

            Pattern[] patterns = new Pattern[2];
            if (search.getPackageNames() != null)
                patterns = getErrorPatterns(search.getPackageNames());
            if (patterns == null || patterns[1] == null) {
                log.log(Level.INFO, String.format("SearchExecutor '%s' (ID: %d) has no package names to search through", search.getName(), search.getId()));
                return events;
            }

            try {
                CSVReader reader = new CSVReader(new StringReader(csvContent));

                String[] line = reader.readNext();

                HashMap<String, Integer> fields = new HashMap<>();
                for (int i = 0; i < line.length; i++) {
                    switch (line[i]) {
                        case "_raw":
                            fields.put("_raw", i);
                            break;
                        case "indextime":
                        case "_indextime":
                            fields.put("indextime", i);
                            break;
                        case "sourcetype":
                        case "_sourcetype":
                            fields.put("sourcetype", i);
                            break;
                        case "source":
                        case "_source":
                            fields.put("source", i);
                            break;
                        case "host":
                        case "_host":
                            fields.put("host", i);
                            break;
                    }
                }

                while ((line = reader.readNext()) != null) {
                    Optional<EventExecution> tempEvent = process(line[fields.get("_raw")], patterns, logFormats).stream().findFirst();
                    if (tempEvent.isPresent() && !eventExecutionService.exists(tempEvent.get())) {
                        EventExecution event = tempEvent.get();
                        if (fields.containsKey("indextime"))
                            event.setIndexedTime(Long.parseLong(line[fields.get("indextime")]) * 1000L);
                        if (fields.containsKey("sourcetype"))
                            event.setSourceType(line[fields.get("sourcetype")]);
                        if (fields.containsKey("source"))
                            event.setSource(line[fields.get("source")]);
                        if (fields.containsKey("host"))
                            event.setHost(line[fields.get("host")]);
                        event.setEnabled(true);
                        event.setAlert(alert);
                        events.add(event);
                    }
                }
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }

            if (events.isEmpty())
                log.log(Level.INFO, "No events were found");
            else
                log.log(Level.INFO, String.format("%d events were found that contained an ERROR level", events.size()));
            return events;

        } catch (Exception ex) {
            log.log(Level.INFO, ex.getMessage());
            return events;
        }
    }

    public List<EventExecution> readFromSplunk(List<HashMap<String, String>> eventsMap, LogAlert alert) {
        List<EventExecution> events = new ArrayList<>();
        SearchExecutor search = alert.getSearchExecutor();

        try {
            List<LogFormat> logFormats = parsePattern(search.getPattern());

            Pattern[] patterns = new Pattern[2];
            if (search.getPackageNames() != null)
                patterns = getErrorPatterns(search.getPackageNames());
            if (patterns == null || patterns[1] == null) {
                log.log(Level.INFO, String.format("SearchExecutor '%s' (ID: %d) has no package names to search through", search.getName(), search.getId()));
                return events;
            }
            
            for (HashMap<String, String> eventMap : eventsMap) {
                Optional<EventExecution> tempEvent = process(eventMap.get("_raw"), patterns, logFormats).stream().findFirst();
                if (tempEvent.isPresent() && !eventExecutionService.exists(tempEvent.get())) {
                    EventExecution event = tempEvent.get();
                    event.setSource(eventMap.get("source"));
                    event.setSourceType(eventMap.get("sourcetype"));
                    event.setIndexedTime(Long.parseLong(eventMap.get("_indextime")) * 1000L);
                    event.setHost(eventMap.get("host"));
                    event.setAlert(alert);
                    events.add(event);
                }
            }

            if (events.isEmpty())
                log.log(Level.INFO, "No events were found");
            else
                log.log(Level.INFO, String.format("%d events were found that contained an ERROR level", events.size()));
            return events;

        } catch (Exception ex) {
            log.log(Level.INFO, ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<EventExecution> read(String content, String packageNames, String layout) {
        if (content == null)
            return null;
        List<String> lines = Arrays.asList(content.split("\\r?\\n(?!\\t|Caused by)"));
        return read(lines, packageNames, layout);
    }

    public List<EventExecution> read(List<String> content, String packageNames, String layout) {
        if (content == null)
            return null;
        try {
            List<LogFormat> logFormats = parsePattern(layout);
            return process(content, getErrorPatterns(packageNames), logFormats);
        } catch (Exception ex) {
            log.log(Level.INFO, ex.getMessage());
            return new ArrayList<>();
        }
    }

    private List<EventExecution> process(String content, Pattern[] patterns, List<LogFormat> logFormats) {
        List<String> lines = Arrays.asList(content.split("\\r?\\n(?!\\t|Caused by)"));
        return process(lines, patterns, logFormats);
    }

    private List<EventExecution> process(List<String> content, Pattern[] patterns, List<LogFormat> logFormats) {
        List<EventExecution> events = new ArrayList<>();
        HashMap<String, String> log;

        if (patterns == null || patterns.length == 0)
            return events;
        
        for (int x = 0; x < content.size(); x++) {
            if ((log = getError(content, x, patterns, logFormats)) != null) {
                EventExecution event = EventExecution.builder()
                        .content(log.get("stackTrace").isEmpty() ? content.get(x) : content.get(x) + "\n" + log.get("stackTrace"))
                        .date(log.get("date") != null ? Long.valueOf(log.get("date")) : null)
                        .build();

                ErrorCase error = errorCaseService.findErrorBySimilarity(log.get("level"), log.get("class"), log.get("message"), log.get("stackTrace"));
                if (error == null) {
                    error = ErrorCase.builder()
                            .level(log.get("level"))
                            .path(log.get("class"))
                            .thread(log.get("thread"))
                            .message(log.get("message"))
                            .stackTrace(log.get("stackTrace"))
                            .build();
                    error = errorCaseService.save(error);
                } else {
                    if (!error.getThread().contains(log.get("thread"))) {
                        error.setThread(error.getThread() + "," + log.get("thread"));
                        error = errorCaseService.update(error);
                    }
                }

                event.setError(error);
                events.add(event);
            }
        }

        return events;
    }

    private HashMap<String, String> getError(List<String> content, int position, Pattern[] patterns, List<LogFormat> logFormatList) {
        String line = content.get(position);

        // Checks if it has an ERROR level
        if (!patterns[0].matcher(line).find())
            return null;

        // Checks if CLASS is contained by given package names
        if (!patterns[1].matcher(line).find())
            return null;

        LogHeader header = new LogHeader(line, DEFAULT_SEPARATOR);
        if (!isLogLine(line, header.getSeparator(), logFormatList))
            return null;

        return this.decompose(content, header, logFormatList, position);
    }

    private boolean isLogLine(String s, String separator, List<LogFormat> logFormats) {
        LogHeader line = new LogHeader(s, separator);
        for (LogFormat logFormat: logFormats) {
            if (!line.hasNext())
                break;
            if (!logFormat.isValid(logFormat.getNext(line).getValue()))
                return false;
        }
        return true;
    }

    private HashMap<String, String> decompose(List<String> content, LogHeader line, List<LogFormat> logFormatList, int position) {
        HashMap<String, String> lineData = new HashMap<>();

        for (LogFormat logFormat: logFormatList) {
            if (!line.hasNext()) // Check if there are no more elements in the line
                break;
            Pair<String, String> data = logFormat.getNext(line); // Get the element parsing
            if (data != null && !data.getKey().equals("literal"))
                lineData.put(data.getKey(), data.getValue()); // Set the values for the "Error" object
        }

        lineData.put("stackTrace", getStackTrace(content, line, logFormatList, position));

        return lineData;
    }

    private String getStackTrace(List<String> content, LogHeader line, List<LogFormat> logFormats, int position) {
        StringBuilder stackTrace = new StringBuilder();
        if (content.size() > position) {
            position++;
            for (; position<content.size(); position++) {
                if (!isLogLine(content.get(position), line.getSeparator(), logFormats)) {
                    if (stackTrace.length() == 0)
                        stackTrace.append(content.get(position));
                    else
                        stackTrace.append("\n").append(content.get(position));
                } else {
                    break;
                }
            }
        }
        return stackTrace.toString();
    }

    private Pattern[] getErrorPatterns(String packageNames) {
        if (packageNames == null || packageNames.isEmpty())
            return null;
        String packageNamesDelimited = String.join("|", packageNames.split("\\s*,\\s*"));
        return new Pattern[]{Pattern.compile("(?<!(\\.|\\B))ERROR(?!(\\.|\\B))"), Pattern.compile("(?<!\\tat )(" + packageNamesDelimited + ")")};
    }

    private List<LogFormat> parsePattern(String pattern) throws Exception{
        List<LogFormat> logFormats = new ArrayList<>();

        if (pattern == null || pattern.isEmpty())
            throw new Exception("The entered pattern is empty");

        Parser<String> parser = new Parser<>(pattern);
        logFormats = doParse(parser.parse(), logFormats);

        if (logFormats.isEmpty() || logFormats.stream().allMatch(logFormat -> logFormat.getFieldName().equals("literal")))
            throw new Exception(String.format("Pattern \"%s\" is invalid", pattern));

        return logFormats;
    }

    private List<LogFormat> doParse(Node node, List<LogFormat> logFormats) {
        switch (node.getType()) {
            case 0:
                logFormats.add(new LiteralLogFormat((String) node.getValue()));
                break;

            case 1:
                SimpleKeywordNode simpleKeywordNode = (SimpleKeywordNode) node;
                if (Arrays.asList("d", "date").contains(simpleKeywordNode.getValue().toString())) {
                    String dateFormat = null;
                    if (simpleKeywordNode.getOptions() != null && !simpleKeywordNode.getOptions().isEmpty())
                        dateFormat = simpleKeywordNode.getOptions().get(0);
                    logFormats.add(new DateLogFormat(dateFormat == null || dateFormat.contains("ISO8601") ? "yyyy-MM-dd HH:mm:ss,SSSS" : dateFormat));
                } else if (Arrays.asList("p", "le", "level").contains(simpleKeywordNode.getValue().toString())) {
                    logFormats.add(new LevelLogFormat());
                } else if (Arrays.asList("t", "thread").contains(simpleKeywordNode.getValue().toString())) {
                    logFormats.add(new ThreadLogFormat());
                } else if (Arrays.asList("C", "class", "c", "lo", "logger").contains(simpleKeywordNode.getValue().toString())) {
                    logFormats.add(new ClassLogFormat());
                } else if (Arrays.asList("m", "msg", "message").contains(simpleKeywordNode.getValue().toString())) {
                    logFormats.add(new MessageLogFormat());
                }
                break;

            case 2:
                CompositeNode compositeNode = (CompositeNode) node;
                logFormats = doParse(compositeNode.getChildNode(), logFormats);
                break;
        }

        if (node.getNext() != null)
            return doParse(node.getNext(), logFormats);
        else
            return logFormats;
    }
}
