package com.clarolab.logtriage.connectors;

import java.util.HashMap;
import java.util.List;

public interface ILogConnector {
    ILogConnector connect(String url, Integer port, String protocol, String token);
    List<HashMap<String, String>> getRecentEvents(String sid, Long fromDate);
}
