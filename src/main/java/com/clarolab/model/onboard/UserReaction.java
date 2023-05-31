package com.clarolab.model.onboard;

import com.clarolab.model.Entry;
import com.clarolab.model.User;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_USER_REACTION;

@Entity
@Table(name = TABLE_USER_REACTION, indexes = {
        @Index(name = "IDX_USER_REACTION_LIST", columnList = "user_id,guide_id"),
        @Index(name = "IDX_USER_REACTION_LIST_ENABLED", columnList = "user_id,guide_id,enabled")
})

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserReaction extends Entry {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    private String answer;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private GuideAnswer answerType;

    @Builder
    private UserReaction(Long id, boolean enabled, long updated, long timestamp, User user, Guide guide, String answer, GuideAnswer answerType) {
        super(id, enabled, updated, timestamp);
        this.user = user;
        this.guide = guide;
        this.answer = answer;
        this.answerType = answerType;
    }

    public boolean wasAnswered() {
        if (answerType == null) {
            return false;
        }
        return answerType.wasAnswered();
    }
}
