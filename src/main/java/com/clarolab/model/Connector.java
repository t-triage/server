/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.connectors.CIConnector;
import com.clarolab.model.types.ConnectorType;
import com.google.common.collect.Lists;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

import static com.clarolab.util.Constants.TABLE_CONNECTOR;

@Entity
@Table(name = TABLE_CONNECTOR, indexes = {
        @Index(name = "IDX_CONNECTOR_NAME", columnList = "name"),
        @Index(name = "IDX_CONNECTOR_USERNAME", columnList = "userName")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Connector extends Entry {

    private String name;
    private String url;
    private String userName;
    private String userToken;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ConnectorType type;

    @OneToMany(mappedBy = "connector", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Container> containers;

    @Builder
    private Connector(Long id, boolean enabled, long updated, long timestamp, String name, String url, String userName, String userToken, ConnectorType type, List<Container> containers) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.url = url;
        this.userName = userName;
        this.userToken = userToken;
        this.type = type;
        this.containers = containers;
    }

    public void add(Container container) {
        initContainers();
        container.setConnector(this);
        this.getContainers().add(container);
    }

    private void initContainers(){
        if (getContainers() == null)
            this.setContainers(Lists.newArrayList());
    }

    public CIConnector getCIConnector() {
        return getType().getConnector(null, this);
    }

    public Optional<Container> getAnyContainer() {
        return containers.stream().findAny();
    }
}
