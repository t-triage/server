package com.clarolab.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_EVOLUTION_GOAL;

@Entity
@Table(name = TABLE_EVOLUTION_GOAL, indexes = {

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrendGoal extends Entry<Executor>  {
    private Long expectedGrowth;
    private Long requiredGrowth;

    private Long expectedTriageDone;
    private Long requiredTriageDone;

    private Long expectedPassing;
    private Long requiredPassing;

    private Long expectedStability;
    private Long requiredStability;

    private Long expectedCommits;
    private Long requiredCommits;

    @Builder
    private TrendGoal(Long id, boolean enabled, long updated, long timestamp, Long expectedGrowth,
                      Long requiredGrowth, Long expectedTriageDone, Long requiredTriageDone, Long expectedPassing, Long requiredPassing,
                      Long expectedStability, Long requiredStability, Long requiredCommits, Long expectedCommits) {

        super(id, enabled, updated, timestamp);
        this.expectedGrowth = expectedGrowth;
        this.requiredGrowth = requiredGrowth;

        this.expectedTriageDone = expectedTriageDone;
        this.requiredTriageDone = requiredTriageDone;

        this.expectedPassing = expectedPassing;
        this.requiredPassing = requiredPassing;

        this.expectedStability = expectedStability;
        this.requiredStability = requiredStability;

        this.requiredCommits = requiredCommits;
        this.expectedCommits = expectedCommits;


    }
}
