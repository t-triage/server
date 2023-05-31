package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.client.BambooApiClient;
import com.clarolab.bamboo.client.BambooProjectClient;
import com.clarolab.bamboo.entities.BambooProject;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.model.Container;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;

@Log
public class BambooProjectService {

    private BambooProjectClient bambooProjectClient;

    @Builder
    private BambooProjectService(BambooApiClient bambooApiClient){
        bambooProjectClient = BambooProjectClient.builder().bambooApiClient(bambooApiClient).build();
        bambooProjectClient.setLimitResults(1000);
    }

    public List<Container> getProjectsAsContainers() throws ContainerServiceException {
        try {
            List<Container> containers = Lists.newArrayList();
            bambooProjectClient.getProjects().forEach(bambooProject -> containers.add(createContainer(bambooProject)));
            return containers;
        } catch (Exception e) {
            throw new ContainerServiceException(String.format("[getProjectsAsContainers] : An error occurred trying to get containers"), e);
        }
    }

    public Container getProjectAsContainer(String containerNameOrUrl) throws ContainerServiceException {
        try {
            if (StringUtils.isURL(containerNameOrUrl)) {
                return createContainer(bambooProjectClient.getProjectFromUrl(containerNameOrUrl));
            } else {
                return createContainer(bambooProjectClient.getProjectFromName(containerNameOrUrl));
            }
        }catch(Exception e){
            throw new ContainerServiceException(String.format("[getProjectAsContainer] : An error occurred trying to get Container(%s)" , containerNameOrUrl), e);
        }
    }

    public Container getProjectIfItExistsAsContainer(String containerName) throws ContainerServiceException {
        try {
            return createContainer(bambooProjectClient.getProjectFromName(containerName));
        } catch (Exception e) {
            throw  new ContainerServiceException(String.format("[getProjectIfItExistsAsContainer] : An error occurred trying to get Container(name=%s)" , containerName), e);
        }
    }

    private Container createContainer(BambooProject bambooProject){
        return Container.builder()
                .name(bambooProject.getName())
                .url(bambooProject.getUrl())
                .hiddenData(bambooProject.getKey())
                .build();
    }

}
