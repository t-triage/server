package com.clarolab.logtriage.connectors.impl.splunk;

import com.clarolab.logtriage.connectors.ILogConnector;
import com.clarolab.logtriage.connectors.LogConnector;
import com.splunk.JobEventsArgs;
import com.splunk.ResultsReaderJson;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log
@Getter
@Setter
@NoArgsConstructor
public class SplunkConnector implements ILogConnector {

    private SplunkApiClient client;

    @Builder
    SplunkConnector(LogConnector connector) {
        String url = connector.getUrl();
        Integer port = connector.getPort();
        String protocol = connector.getProtocol();
        String sessionKey = connector.getToken();

        this.connect(url, port, protocol, sessionKey);
    }

    @Override
    public SplunkConnector connect(String url, Integer port, String protocol, String token) {
        setClient(new SplunkApiClient(url, port, protocol, token));
        return this;
    }

    public Integer getEventsCount(String sid) {
        return this.getClient() == null ? null : client.getEventsCount(sid);
    }

    @Override
    public List<HashMap<String, String>> getRecentEvents(String sid, Long fromDate) {
        int count = getClient().getMaxResultsRow() < 10000 ? getClient().getMaxResultsRow() : 10000;
        int offset = 0;

        ArrayList<HashMap<String, String>> events = new ArrayList<>();

        while (true) {
            events.addAll(getEvents(sid, Long.toString(fromDate/1000L), offset, count));
            if (!events.isEmpty() && (events.get(0).get("_serial").equals("0") || events.get(events.size()-1).get("_serial").equals("0")))
                break;
            offset += count;
        }

        return events;
    }

    public List<HashMap<String, String>> getEvents(String sid, String fromDate, int offset, int count) {
        if (this.getClient() == null)
            return null;

        String[] fields = {"_raw", "_time", "_indextime", "source", "sourcetype", "host", "_serial"};

        JobEventsArgs args = new JobEventsArgs();
        args.setEarliestTime(fromDate);
        args.setCount(count);
        args.setOffset(offset);
        args.setFieldList(fields);
        if (client.getJob(sid).getEventSorting().equals("desc"))
            args.setSearch("| reverse");

        return getEvents(sid, args);
    }

    private List<HashMap<String, String>> getEvents(String sid, JobEventsArgs args) {
        List<HashMap<String, String>> events = new ArrayList<>();
        args.setOutputMode(JobEventsArgs.OutputMode.JSON);

        try {
            InputStream input = client.getJob(sid).getEvents(args);
            ResultsReaderJson reader = new ResultsReaderJson(input);
            HashMap<String, String> event;
            while ((event = reader.getNextEvent()) != null)
                events.add(event);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

}
