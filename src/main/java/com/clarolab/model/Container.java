/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.ReportType;
import com.google.common.collect.Lists;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_CONTAINER;

@Entity
@Table(name = TABLE_CONTAINER, indexes = {
        @Index(name = "IDX_CONTAINER_NAME", columnList = "name"),
        @Index(name = "IDX_CONTAINER_NAME", columnList = "product_id"),
        @Index(name = "IDX_CONTAINER_ENABLED", columnList = "enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Container extends Entry {

    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    //Internal information that should not be exposed and used only in backend
    @Column(name = "hidden_data")
    @Nullable
    private String hiddenData;
    private String url;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Executor> executors;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_id", nullable = false)
    private Connector connector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private PopulateMode populateMode;
    
    @Enumerated
    @Column(columnDefinition = "smallint")
    private ReportType reportType;
    

    @Builder
    private Container(Long id, boolean enabled, long updated, long timestamp, String name, String description, String hiddenData, String url, List<Executor> executors, Connector connector, Product product, PopulateMode populateMode, ReportType reportType) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.hiddenData = hiddenData;
        this.url = url;
        this.executors = executors;
        this.connector = connector;
        this.product = product;
        this.populateMode = populateMode;
        this.reportType = reportType;
    }

    public void add(Executor executor) {
        initExecutors();
        executor.setContainer(this);
        this.getExecutors().add(executor);
    }

    public void add(List<Executor> executors) {
        initExecutors();
        executors.forEach(executor -> this.add(executor));
    }

   public Connector getConnector() {
        return connector;
    }

    public boolean isActive() {
        return isEnabled() && connector.isEnabled() && product.isEnabled();
    }

    public String getProductName() {
        return product.getName();
    }

    public String[] getHiddenData(){
        return this.hiddenData.split("/");
    }

    public String getRawHiddenData() {
        return hiddenData;
    }

    public List<Executor> getExecutors() {
        if (executors == null) {
            this.setExecutors(Lists.newArrayList());
        }
        return executors;
    }

    public String getConnectorName() {
        return getConnector().getName();
    }

    public boolean isHierarchicalyEnabled(){
        return isActive();
    }

    private void initExecutors(){
        if (getExecutors() == null)
            this.setExecutors(Lists.newArrayList());
    }

    public CIConnector getCIConnector() {
        ApplicationContextService context = ApplicationContextService.builder().product(getProduct()).container(this).build();
        return getConnector().getType().getConnector(context, getConnector());
    }

    public boolean isPullMode() {
        return getPopulateMode().equals(PopulateMode.PULL);
    }

    public boolean isPushMode() {
        return getPopulateMode().equals(PopulateMode.PUSH);
    }

    public PopulateMode getPopulateMode(){
        if(this.populateMode==null)
            populateMode = PopulateMode.PULL;
        return populateMode;
    }

    public boolean hasExecutor(String executorName) {
        return getExecutors().stream().anyMatch(executor -> executor.getName().equals(executorName));
    }
    
    public boolean hasReportType() {
        return reportType != null && !ReportType.UNKNOWN.equals(reportType);
    }
}
