package com.clarolab.event.analytics;

import com.clarolab.model.Entry;
import com.clarolab.model.Product;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_STAT_PRODUCT;

@Entity
@Table(name = TABLE_STAT_PRODUCT, indexes = {
        @Index(name = "IDX_PRODUCT_DATE", columnList = "actualDate"),
        @Index(name = "IDX_PRODUCT_ID", columnList = "product_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStat extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String actualDate;

    private long pass;
    private long skip;
    private long newFails;
    private long fails;
    private long nowPassing;
    private long toTriage;
    private long totalTests;
    private long autoTriaged;
    private Integer commits;
    private Integer repositoryLinesChanged;

    private double duration;
    private double stabilityIndex;
    private String executionDate;

    private String deadline;
    private int daysToDeadline;
    private int deadlinePriority;

    private int evolutionPass;
    private int evolutionSkip;
    private int evolutionNewFails;
    private int evolutionFails;
    private int evolutionNowPassing;
    private int evolutionToTriage;

    private long maxExecutedTest;

    @Builder
    private ProductStat(Long id, boolean enabled, long updated, long timestamp, long totalTests, Product product, String actualDate, long pass, long skip, long newFails, long fails, long nowPassing, long toTriage, double duration, double stabilityIndex, String executionDate, String deadline, int daysToDeadline, int deadlinePriority, int evolutionPass, int evolutionSkip, int evolutionNewFails, int evolutionFails, int evolutionNowPassing, int evolutionToTriage, long maxExecutedTest, long autoTriaged, Integer commits, Integer repositoryLinesChanged) {
        super(id, enabled, updated, timestamp);
        this.product = product;
        this.actualDate = actualDate;
        this.pass = pass;
        this.skip = skip;
        this.newFails = newFails;
        this.fails = fails;
        this.nowPassing = nowPassing;
        this.toTriage = toTriage;
        this.duration = duration;
        this.stabilityIndex = stabilityIndex;
        this.executionDate = executionDate;
        this.deadline = deadline;
        this.daysToDeadline = daysToDeadline;
        this.deadlinePriority = deadlinePriority;
        this.evolutionPass = evolutionPass;
        this.evolutionSkip = evolutionSkip;
        this.evolutionNewFails = evolutionNewFails;
        this.evolutionFails = evolutionFails;
        this.evolutionNowPassing = evolutionNowPassing;
        this.evolutionToTriage = evolutionToTriage;
        this.totalTests = totalTests;
        this.maxExecutedTest = maxExecutedTest;
        this.autoTriaged = autoTriaged;
        this.commits = commits;
        this.repositoryLinesChanged = repositoryLinesChanged;
    }
}
