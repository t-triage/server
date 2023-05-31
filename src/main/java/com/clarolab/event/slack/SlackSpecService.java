/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.slack;

import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.service.ContainerService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log
public class SlackSpecService extends BaseService<SlackSpec> {

    @Autowired
    private SlackSpecRepository slackSpecRepository;

    @Autowired
    private SlackService slackService;

    @Autowired
    private ContainerService containerService;

    @Override
    public BaseRepository<SlackSpec> getRepository() {
        return slackSpecRepository;
    }

    public SlackSpec find(Executor executor) {
        SlackSpec spec = slackSpecRepository.findByExecutor(executor);
        if (spec == null) { // Cambio de == por !=
            spec = slackSpecRepository.findByProductAndContainerAndExecutor(executor.getProduct(), executor.getContainer(), executor);//Cambio de null por Executor
        }
        if (spec != null) {
            spec = slackSpecRepository.findByProductAndContainerAndExecutor(executor.getProduct(), executor.getContainer(), executor);
        }
        return spec;
    }

    public SlackSpec find(Container container) {
        SlackSpec spec =  slackSpecRepository.findByProductAndContainerAndExecutor(container.getProduct(), container, null);
        
        if (spec == null) {
            spec = slackSpecRepository.findByProductAndContainerAndExecutor(container.getProduct(), null, null);
        }
        return spec;
    }

    public SlackSpec find(Product product) {
        return slackSpecRepository.findByProductAndContainerAndExecutor(product, null, null);
    }

    public String sendTestMessage(Container container) {
        SlackSpec spec = find(container);
        return sendTestMessage(spec);
    }

    public String sendTestMessage(SlackSpec spec) {
        return sendTestMessage(spec, spec.getFinalChannel(), "t-Triage Test Message");
    }

    public String sendTestMessage(SlackSpec spec, String channel, String text) {
        return slackService.sendMessage(spec, channel, text, true);
    }

    public List<SlackSpec> findAllEnabled() {
        List<SlackSpec> answer = findAll();
        return answer.stream().filter(slackSpec -> slackSpec.isHierarchicalyEnabled()).collect(Collectors.toList());
    }

    public Map<Container, SlackSpec> findAllContainers() {
        List<SlackSpec> allSpecs = findAllEnabled();
        List<Container> allContainers = containerService.findAllEnabled();
        Map<Container, SlackSpec> map = new HashMap<>();
        SlackSpec productSpec;
        SlackSpec containerSpec;
        SlackSpec executorSpec;
        SlackSpec existingSpec;

        for (Container container : allContainers) {
            // Try to find without any executor
            existingSpec = null;
            productSpec = null;
            containerSpec = null;
            executorSpec = null;
            for (SlackSpec spec : allSpecs) {
                if (spec.getContainer() == container && spec.getExecutor() == null) {
                    containerSpec = spec;
                }
                if (spec.getContainer() == container && spec.getExecutor() != null) {
                    executorSpec = spec;
                }
                if (spec.getProduct() == container.getProduct() && spec.getContainer() == null) {
                    productSpec = spec;
                }
            }
            if (containerSpec != null) {
                existingSpec = containerSpec;
            } else if (productSpec != null) {
                existingSpec = productSpec;
            } else {
                existingSpec = executorSpec;
            }
            map.put(container, existingSpec);
        }

        return map;
    }


}
