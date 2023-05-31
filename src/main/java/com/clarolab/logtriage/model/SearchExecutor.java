package com.clarolab.logtriage.model;

import com.clarolab.logtriage.connectors.LogConnector;
import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_SEARCH_EXECUTOR;

@Entity
@Table(name = TABLE_SEARCH_EXECUTOR, indexes = {
        @Index(name = "IDX_SEARCHEXECUTOR_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchExecutor extends Entry {

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String search;

    @Type(type = "org.hibernate.type.TextType")
    private String url;

    @Type(type = "org.hibernate.type.TextType")
    private String pattern;

    // Should be replaced by Product.packageNames?
    @Type(type = "org.hibernate.type.TextType")
    private String packageNames;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "connector_id")
    private LogConnector logConnector;

    @OneToMany(mappedBy = "searchExecutor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LogAlert> alerts;

    @Builder
    public SearchExecutor(Long id, boolean enabled, long updated, long timestamp, String name, String search, String url, String pattern, String packageNames, LogConnector logConnector, List<LogAlert> alerts) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.search = search;
        this.url = url;
        this.pattern = pattern;
        this.packageNames = packageNames;
        this.logConnector = logConnector;
        this.alerts = alerts == null ? new ArrayList<>() : alerts;
    }
}
