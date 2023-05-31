package com.clarolab.event.analytics;

import com.clarolab.model.Entry;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_STAT_MANUAL_TEST;

@Entity
@Table(name = TABLE_STAT_MANUAL_TEST, indexes = {
        @Index(name = "IDX_MANUAL_TEST_TIMESTAMP", columnList = "timestamp") }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestStat extends Entry {

    private Long totalTests;
    private Long executed;
    private Long pass;
    private Long fails;

    @Builder
    private ManualTestStat(Long id, boolean enabled, long updated, long timestamp, Long totalTests, Long executed, Long pass, Long fails) {
        super(id, enabled, updated, timestamp);
        this.totalTests = totalTests;
        this.executed = executed;
        this.pass = pass;
        this.fails = fails;
    }
}
