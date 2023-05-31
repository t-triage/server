package com.clarolab.logtriage.connectors;

import com.clarolab.model.Entry;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "QA_LOG_CONNECTOR")
@Getter
@Setter
@NoArgsConstructor
public class LogConnector extends Entry {

    private String url;
    private String protocol;
    private Integer port;
    private String token;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private LogConnectorType type;

    @Builder
    public LogConnector(String url, String protocol, Integer port, String token, LogConnectorType type) {
        this.url = url;
        this.protocol = protocol;
        this.port = port;
        this.token = token;
        this.type = type;
    }

    public ILogConnector getConnector() {
        return getType().getConnector(this);
    }


}
