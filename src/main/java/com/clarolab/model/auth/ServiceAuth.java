/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.auth;

import com.clarolab.model.Connector;
import com.clarolab.model.Entry;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_SERVICE_AUTH;

@Entity
@Table(name = TABLE_SERVICE_AUTH, indexes = {
        @Index(name = "IDX_SERVICE_AUTH_CLIENT_ID", columnList = "clientId", unique = true),
        @Index(name = "IDX_SERVICE_AUTH_CONNECTOR_ID", columnList = "connector_id", unique = true)
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAuth extends Entry<ServiceAuth> {

    private String clientId;
    private String secretId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_id")
    private Connector connector;

    @Builder
    private ServiceAuth(Long id, boolean enabled, long updated, long timestamp, String clientId, String secretId, Connector connector) {
        super(id, enabled, updated, timestamp);
        this.clientId = clientId;
        this.secretId = secretId;
        this.connector = connector;
    }

}
