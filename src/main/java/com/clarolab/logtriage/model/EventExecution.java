package com.clarolab.logtriage.model;

import com.clarolab.model.Entry;
import lombok.*;
import lombok.extern.java.Log;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_EVENT_EXECUTION;

@Entity
@Table(name = TABLE_EVENT_EXECUTION, indexes = {
        @Index(name = "IDX_EVENTEXECUTION_ERROR", columnList = "error_id"),
        @Index(name = "IDX_EVENTEXECUTION_HOST_SOURCETYPE_SOURCE", columnList = "host,sourceType,source")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Log
public class EventExecution extends Entry {

    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Type(type = "org.hibernate.type.TextType")
    private String host;

    @Type(type = "org.hibernate.type.TextType")
    private String source;

    @Type(type = "org.hibernate.type.TextType")
    private String sourceType;

    // The date when the event happened
    private Long date;

    // The date when the event was indexed into Splunk, ElasticSearch, etc..
    private Long indexedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logAlert_id")
    private LogAlert alert;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "error_id", nullable = false)
    private ErrorCase error;

    @Builder
    public EventExecution(Long id, boolean enabled, long updated, long timestamp, String content, String host, String source, String sourceType, Long date, Long indexedTime, LogAlert alert, ErrorCase error) {
        super(id, enabled, updated, timestamp);
        this.content = content;
        this.host = host;
        this.source = source;
        this.sourceType = sourceType;
        this.date = date;
        this.indexedTime = indexedTime;
        this.alert = alert;
        this.error = error;
    }
}
