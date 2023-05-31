package com.clarolab.logtriage.service;

import com.clarolab.logtriage.connectors.LogConnector;
import com.clarolab.logtriage.connectors.LogConnectorType;
import com.clarolab.logtriage.dto.SplunkAlertDTO;
import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.repository.LogAlertRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log
public class LogAlertService extends BaseService<LogAlert> {

    @Autowired
    private LogConnectorService logConnectorService;

    @Autowired
    private SearchExecutorService searchExecutorService;

    @Autowired
    private EventExecutionImportService importService;

    @Autowired
    private EventExecutionService eventExecutionService;

    @Autowired
    private LogAlertRepository repository;

    @Override
    protected BaseRepository<LogAlert> getRepository() {
        return this.repository;
    }

    public Boolean push(SplunkAlertDTO alertDTO) {
        SearchExecutor searchExecutor = searchExecutorService.find(alertDTO.getSearchName());

        if (searchExecutor == null) {
            HashMap<String, String> connection = parseServerUrl(alertDTO.getServerUri());
            LogConnector connector = LogConnector.builder()
                    .url(connection.get("url"))
                    .port(Integer.parseInt(connection.get("port")))
                    .protocol(connection.get("protocol"))
                    .token(alertDTO.getSessionKey())
                    .type(LogConnectorType.SPLUNK)
                    .build();

            searchExecutor = SearchExecutor.builder()
                    .enabled(true)
                    .name(alertDTO.getSearchName())
                    .pattern(alertDTO.getPattern())
                    .packageNames(alertDTO.getPackageNames())
                    .logConnector(connector)
                    .build();
            searchExecutor = searchExecutorService.save(searchExecutor);
        } else {
            if (alertDTO.getPattern() != null && (searchExecutor.getPattern() == null || !searchExecutor.getPattern().equals(alertDTO.getPattern())))
                searchExecutor.setPattern(alertDTO.getPattern());
            if (alertDTO.getPackageNames() != null && (searchExecutor.getPackageNames() == null || !searchExecutor.getPackageNames().equals(alertDTO.getPackageNames())))
                searchExecutor.setPackageNames(alertDTO.getPackageNames());
            searchExecutor.getLogConnector().setToken(alertDTO.getSessionKey());
            searchExecutorService.update(searchExecutor);
        }

        LogAlert logAlert = LogAlert.builder()
                .enabled(true)
                .host(alertDTO.getServerHost())
                .appName(alertDTO.getApp())
                .owner(alertDTO.getOwner())
                .date(alertDTO.getAlertTime())
                .lastCheck(alertDTO.getAlertTime())
                .sid(alertDTO.getSid())
                .url(alertDTO.getServerUri())
                .searchExecutor(searchExecutor)
                .build();

        log.log(Level.INFO, String.format("Processing Splunk events for search '%s'", searchExecutor.getName()));
        List<EventExecution> events = importService.readFromCVS(alertDTO.getEvents(), logAlert);

        if (!events.isEmpty()) {
            logAlert.setEvents(events);
            logAlert = save(logAlert);
        } else {
            logAlert = latestCheckedAlert();
            if (logAlert != null) {
                logAlert.setLastCheck(alertDTO.getAlertTime());
                logAlert = update(logAlert);
            }
        }

        return logAlert != null;
    }

    private HashMap<String, String> parseServerUrl(String serverUri) {
        Pattern pattern = Pattern.compile("^((?<protocol>\\w+):/+)?(?<url>[\\w.-]+):?(?<port>\\d+)?(/(.*))?");
        Matcher matcher = pattern.matcher(serverUri);

        HashMap<String, String> data = new HashMap<>();

        if (matcher.find()) {
            data.put("protocol", matcher.group("protocol"));
            data.put("url", matcher.group("url"));
            data.put("port", matcher.group("port") != null ? matcher.group("port") : "8089");
        }

        return data;
    }

    public Long latestCheckedAlertDate() {
        Long date = this.repository.latestCheckedAlertDate();
        return date == null ? 0L : date;
    }

    public LogAlert latestCheckedAlert() {
        return this.repository.findTopByOrderByLastCheckDesc();
    }

}
