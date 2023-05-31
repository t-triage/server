package com.clarolab.logtriage.model;

import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_ERROR_CASE;

@Entity
@Table(name = TABLE_ERROR_CASE, indexes = {
        @Index(name = "IDX_ERRORCASE_LEVEL_PATH", columnList = "level,path"),
        @Index(name = "IDX_ERRORCASE_LEVEL", columnList = "level")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCase extends Entry {

    private String level;

    @Type(type = "org.hibernate.type.TextType")
    private String path;

    @Type(type = "org.hibernate.type.TextType")
    private String thread;

    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Type(type = "org.hibernate.type.TextType")
    private String stackTrace;

    @OneToMany(mappedBy = "error", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EventExecution> events;

    @Builder
    public ErrorCase(Long id, boolean enabled, long updated, long timestamp, String level, String path, String thread, String message, String stackTrace, List<EventExecution> events) {
        super(id, enabled, updated, timestamp);
        this.level = level;
        this.path = path;
        this.thread = thread;
        this.message = message;
        this.stackTrace = stackTrace;
        this.events = events == null ? new ArrayList<>() : events;
    }
}
