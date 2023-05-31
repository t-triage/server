package com.clarolab.event.analytics;

import com.clarolab.model.Entry;
import com.clarolab.model.User;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_STAT_USER;

@Entity
@Table(name = TABLE_STAT_USER, indexes = {
        @Index(name = "IDX_USER_DATE", columnList = "actualDate"),
        @Index(name = "IDX_USER_ID", columnList = "user_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStat extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String actualDate;

    private Integer testsUpdated;
    private Integer linesUpdated;
    private Integer commits;

    @Builder
    public UserStat(Long id, boolean enabled, long updated, long timestamp, User user, String actualDate, Integer testsUpdated, Integer linesUpdated, Integer commits) {
        super(id, enabled, updated, timestamp);
        this.user = user;
        this.actualDate = actualDate;
        this.testsUpdated = testsUpdated;
        this.linesUpdated = linesUpdated;
        this.commits = commits;
    }
}
