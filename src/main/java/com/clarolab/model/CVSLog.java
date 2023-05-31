package com.clarolab.model;

import com.clarolab.model.types.LogType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_LOG;

@Entity
@Table(name = TABLE_LOG, indexes = {
        @Index(name = "IDX_LOG_PRODUCT", columnList = "product_id"),
        @Index(name = "IDX_LOG_AUTHOR", columnList = "author_id"),
        @Index(name = "IDX_LOG_LOGTYPE", columnList = "logType"),
        @Index(name = "IDX_LOG_COMMITHASH", columnList = "commitHash"),
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CVSLog extends Entry {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private TestCase test;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cvsRepository_id")
    private CVSRepository cvsRepository;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private LogType logType;

    @Type(type = "org.hibernate.type.TextType")
    private String authorText;

    @Type(type = "org.hibernate.type.TextType")
    private String authorRealname;

    @Type(type = "org.hibernate.type.TextType")
    private String approverText;

    @Type(type = "org.hibernate.type.TextType")
    private String commitHash;

    // unixtimestamp of the commit date
    private long commitDate;

    // commitDate converted to MM-dd format
    private String commitDay;

    @Type(type = "org.hibernate.type.TextType")
    private String codeModified;

    @Type(type = "org.hibernate.type.TextType")
    private String locationPath;

    private int updatedLines;

    @Builder
    public CVSLog(Long id, boolean enabled, long updated, long timestamp, User author, Report report, String authorRealname, TestCase test, CVSRepository cvsRepository, Product product, LogType logType, String authorText, String approverText, String commitHash, long commitDate, String commitDay, String codeModified, String locationPath, int updatedLines) {
        super(id, enabled, updated, timestamp);
        this.author = author;
        this.test = test;
        this.cvsRepository = cvsRepository;
        this.product = product;
        this.logType = logType;
        this.authorText = authorText;
        this.authorRealname = authorRealname;
        this.approverText = approverText;
        this.commitHash = commitHash;
        this.commitDate = commitDate;
        this.commitDay = commitDay;
        this.codeModified = codeModified;
        this.locationPath = locationPath;
        this.updatedLines = updatedLines;
        this.report = report;
    }

}
