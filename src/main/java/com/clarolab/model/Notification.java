package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_NOTIFICATION;

@Entity
@Table(name = TABLE_NOTIFICATION, indexes = {
    @Index(name = "IDX_NOTIFICATION_LIST", columnList = "user_id,description,enabled,timestamp")
    }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Entry<Notification> {

    @Type(type = "org.hibernate.type.TextType")
    private String subject;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean seen;

    private Integer priority;

    @Builder
    public Notification(Long id, boolean enabled, long updated, long timestamp, String subject, String description, User user, Boolean seen, Integer priority) {
        super(id, enabled, updated, timestamp);
        this.subject = subject;
        this.description = description;
        this.user = user;
        this.seen = seen;
        this.priority = priority;
    }
}
