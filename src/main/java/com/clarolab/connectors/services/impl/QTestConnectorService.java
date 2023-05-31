package com.clarolab.connectors.services.impl;

import com.clarolab.connectors.services.ConnectorService;
import lombok.Data;

//import com.clarolab.qtest.client.QTestApiClient;
//import com.clarolab.qtest.client.QTestServerClient;

@Data
public class QTestConnectorService implements ConnectorService {

//    private QTestApiClient qTestApiClient;
//
//    @Builder
//    private QTestConnectorService(QTestApiClient qTestApiClient){
//        this.qTestApiClient = qTestApiClient;
//    }

    @Override
    public void cleanConnector() {

    }

    @Override
    public boolean getClientServiceStatus() {
        return false;
//        try {
//            return CollectionUtils.isNotEmpty(QTestServerClient.builder().qTestApiClient(qTestApiClient).build().getUserProfiles());
//        }catch (Exception e){
//            return false;
//        }
    }
}
