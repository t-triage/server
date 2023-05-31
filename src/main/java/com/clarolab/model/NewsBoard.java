package com.clarolab.model;

import com.clarolab.event.process.ApplicationEventType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_NEWS_BOARD;

@Entity
@Table(name = TABLE_NEWS_BOARD, indexes = {
        @Index(name = "IDX_BOARD_EVENT", columnList = "eventTime")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewsBoard extends Entry {
    String message;
    long eventTime;
    ApplicationEventType type;

    @Builder
    private NewsBoard(Long id, boolean enabled, long updated, long timestamp, String message, long eventTime, ApplicationEventType type) {
        super(id, enabled, updated, timestamp);
        this.message = message;
        this.eventTime = eventTime;
        this.type = type;
    }
}
