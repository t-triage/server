package com.clarolab.logtriage.connectors.impl.splunk;

import com.splunk.*;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
@Getter
public class SplunkApiClient {

    private Service splunkService;
    private Integer maxResultsRow;

    public SplunkApiClient(String url, Integer port, String protocol, String token) {
        this.connect(url, port, protocol, token);
    }

    public Service connect(String url, Integer port, String protocol, String token) {
        if (protocol != null && protocol.equals("https"))
            HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
        ServiceArgs serviceArgs = new ServiceArgs();
        serviceArgs.setHost(url);
        serviceArgs.setPort(port);
        serviceArgs.setScheme(protocol);
        serviceArgs.setToken("Splunk " + token);

        try {
            this.splunkService = Service.connect(serviceArgs);
            this.maxResultsRow = Integer.parseInt((String) getSplunkService().getConfs().get("limits").get("restapi").get("maxresultrows"));
        } catch (Exception ex) {
            log.log(Level.INFO, String.format("Couldn't connect to Splunk API on %s://%s:%s", protocol, url, port));
            this.clean();
        }

        return this.splunkService;
    }

    public boolean isRunning() {
        return getSplunkService() != null;
    }

    public void disconnect() {
        this.splunkService.logout();
        this.clean();
    }

    private void clean() {
        this.splunkService = null;
        this.maxResultsRow = null;
    }

    public Job getJob(String sid) {
        return isRunning() ? this.getSplunkService().getJob(sid) : null;
    }

    public Integer getEventsCount(String sid) {
        return isRunning() ? this.getSplunkService().getJob(sid).getEventCount() : null;
    }

}
