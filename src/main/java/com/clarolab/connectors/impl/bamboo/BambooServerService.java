package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.client.BambooApiClient;
import com.clarolab.bamboo.client.BambooServerClient;
import lombok.Builder;

public class BambooServerService {

    private BambooServerClient bambooServerClient;

    @Builder
    private BambooServerService(BambooApiClient bambooApiClient){
        bambooServerClient = BambooServerClient.builder().bambooApiClient(bambooApiClient).build();
    }

    public boolean isServerActive(){
        return bambooServerClient.isServerRunning();
    }
}
