package com.clarolab.logtriage.model;

import com.clarolab.model.Entry;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_LOG_ALERT;

@Entity
@Table(name = TABLE_LOG_ALERT, indexes = {
        @Index(name = "IDX_LOGALERT_HOST_APPNAME", columnList = "host,appName"),
        @Index(name = "IDX_LOGALERT_OWNER", columnList = "owner")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogAlert extends Entry {

    private String sid;
    private String appName;
    private String owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "searchExecutor_id", nullable = false)
    private SearchExecutor searchExecutor;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EventExecution> events;

    private Long date;
    private Long lastCheck;

    private String host;
    private String url;

    @Builder
    public LogAlert(Long id, boolean enabled, long updated, long timestamp, String sid, String appName, String owner, SearchExecutor searchExecutor, List<EventExecution> events, Long date, Long lastCheck, String host, String url) {
        super(id, enabled, updated, timestamp);
        this.sid = sid;
        this.appName = appName;
        this.owner = owner;
        this.searchExecutor = searchExecutor;
        this.events = events;
        this.date = date;
        this.lastCheck = lastCheck;
        this.host = host;
        this.url = url;
    }
}
