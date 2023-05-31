package com.clarolab.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_EXTERNAL_BUILD_TRIAGE;

@Entity
@Table(name = TABLE_EXTERNAL_BUILD_TRIAGE, indexes = {
        @Index(name = "IDX_EXTERNALBUILDTRIAGE_LIST", columnList = "suiteName,buildNumber,executedTime")
}
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ExternalBuildTriage extends Entry {
    // Suite
    private String suiteId;
    private String suiteName;
    private int priority;

    // Build
    private int buildNumber;
    private long executedTime;
    private String productBuildVersion;

    // Triage Build
    private long triageDeadline;
    private boolean triaged;
    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User triager;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    // Tests
    private long totalNewFails;
    private long totalNewPass;
    private long totalPass;
    private long totalNotExecuted;
    private long totalTriageDone;
    private long totalFails;
    private long totalTests;

    @Builder
    private ExternalBuildTriage(Long id, boolean enabled, long updated, long timestamp, String suiteId, String suiteName, int priority, int buildNumber, long executedTime, String productBuildVersion, long triageDeadline, boolean triaged, User triager, Note note, long totalNewFails, long totalPass, long totalNewPass, long totalNotExecuted, long totalTriageDone, long totalFails, long totalTests) {
        super(id, enabled, updated, timestamp);
        this.suiteId = suiteId;
        this.suiteName = suiteName;
        this.priority = priority;
        this.buildNumber = buildNumber;
        this.executedTime = executedTime;
        this.productBuildVersion = productBuildVersion;
        this.triageDeadline = triageDeadline;
        this.triaged = triaged;
        this.triager = triager;
        this.note = note;
        this.totalNewFails = totalNewFails;
        this.totalNewPass = totalNewPass;
        this.totalPass = totalPass;
        this.totalNotExecuted = totalNotExecuted;
        this.totalTriageDone = totalTriageDone;
        this.totalFails = totalFails;
        this.totalTests = totalTests;
    }

    public void setTotalTests() {
        totalTests = totalFails + totalNewFails + totalNewPass + totalNotExecuted + totalTriageDone + totalPass;
    }


}
