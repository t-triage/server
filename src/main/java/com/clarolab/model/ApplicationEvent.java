/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.event.process.ApplicationEventType;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_EVENT;

@Entity
@Table(name = TABLE_EVENT, indexes = {
        @Index(name = "IDX_EVENT_PENDING", columnList = "eventTime, processed"),
        @Index(name = "IDX_EVENT_PENDING_TYPE", columnList = "type, processed, extraParameter")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEvent extends Entry {
    // If the event was already processed
    private boolean processed = false;
    // If the event was processed successfully
    private boolean success = false;
    private String processingMessage;
    private long eventTime;

    // Any name just for debugging or displaying purposes
    private String displayName;

    // Any type that can be useful to the processor
    @Enumerated
    @Column(columnDefinition = "smallint")
    private ApplicationEventType type;

    // The class that has originated the event
    private String originatingClass;

    // The method that has originated the event
    private String originatingMethod;

    // The object that has originated the event
    @ManyToOne(fetch = FetchType.LAZY)
    private Entry source;

    // Some optional parameters involved in the event
    @ManyToOne(fetch = FetchType.EAGER)
    private Entry parameter;

    private String extraParameter;
    private String additionalParameter;

    @Builder
    private ApplicationEvent(Long id, boolean enabled, long updated, long timestamp, boolean processed, boolean success, String processingMessage, long eventTime, String displayName, ApplicationEventType type, String originatingClass, String originatingMethod, Entry source, Entry parameter, String extraParameter, String additionalParameter) {
        super(id, enabled, updated, timestamp);
        this.processed = processed;
        this.success = success;
        this.processingMessage = processingMessage;
        this.eventTime = eventTime;
        this.displayName = displayName;
        this.type = type;
        this.originatingClass = originatingClass;
        this.originatingMethod = originatingMethod;
        this.source = source;
        this.parameter = parameter;
        this.extraParameter = extraParameter;
        this.additionalParameter = additionalParameter;
    }
}
