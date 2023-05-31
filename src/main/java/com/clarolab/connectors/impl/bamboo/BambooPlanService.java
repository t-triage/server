package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.client.BambooApiClient;
import com.clarolab.bamboo.client.BambooPlanClient;
import com.clarolab.bamboo.entities.BambooPlan;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class BambooPlanService {

    private BambooPlanClient bambooPlanClient;

    @Builder
    private BambooPlanService(BambooApiClient bambooApiClient){
        bambooPlanClient = BambooPlanClient.builder().bambooApiClient(bambooApiClient).build();
        bambooPlanClient.setLimitResults(1000);
        bambooPlanClient.getBambooProjectClient().setLimitResults(bambooPlanClient.getLimitResults());
    }

    public List<Executor> getAllPlansForProjectAsExecutors(String projectName) throws ExecutorServiceException {
        try {
            return createExecutors(bambooPlanClient.getPlansForProject(projectName));
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[getAllPlansForProjectAsExecutors] : An error occurred trying to get executors at Container(name=%s)]", projectName), e);
        }
    }

    public List<Executor> getNewPlansOnProjectAsExecutors(Container container) throws ExecutorServiceException {
        List<Executor> newExecutors = Lists.newArrayList();
        try {
            List<BambooPlan> plans = bambooPlanClient.getPlansForProject(container.getName());
            List<BambooPlan> filteredPlans = plans.stream()
                    .filter(plan -> LogicalCondition.NOT(container.getExecutors().contains(Executor.builder().name(plan.getName()).hiddenData(plan.getPlanKey()).build())))
                    .collect(Collectors.toList());
            filteredPlans
                    .forEach(plan -> {
                        log.log(Level.INFO, "This is a new executor to be added: " + plan.getName());
                        newExecutors.add(createExecutor(plan));
                    });
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[getNewPlansOnProjectAsExecutors] : An error occurred trying to get new executors fo Container(name=%s)]", container.getName()), e);
        }
        return newExecutors;
    }

    public Map<String, List<Executor>> getPlansOnProjectAsExecutorsGroupedByNewAndOld(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        try {
            Map<String, List<Executor>> map = Maps.newHashMap();
            List<String> executors = container.getExecutors().stream().map(Executor::getName).collect(Collectors.toList());
            bambooPlanClient.getPlansForProject(container.getName()).forEach(plan ->{
                if(executors.contains(plan.getName())){
                    List<Executor> executorList = map.get("old");
                    executorList.add(createExecutor(plan));
                    map.put("old", executorList);
                }else{
                    List<Executor> executorList = map.get("new");
                    executorList.add(createExecutor(plan));
                    map.put("new", executorList);
                }
            });
            return map;
        } catch (Exception e) {
            throw new ExecutorServiceException(String.format("[getPlansOnProjectAsExecutorsGroupedByNewAndOld] : An error occurred trying to get new executors fo Container(name=%s)]", container.getName()), e);
        }
    }

    public boolean isAnExistingPlan(String planName) throws Exception {
        return bambooPlanClient.getPlan(planName) != null;
    }

    public boolean isPlanOnProject(String planName, String projectName) throws Exception {
        return bambooPlanClient.getPlansForProject(projectName).stream().anyMatch(plan -> plan.getName().equals(planName));
    }

    private Executor createExecutor(BambooPlan bambooPlan){
        return Executor.builder()
                .name(bambooPlan.getName())
                .description(bambooPlan.getDescription())
                .hiddenData(bambooPlan.getKey())
                .url(bambooPlan.getUrl())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

    private List<Executor> createExecutors(List<BambooPlan> bambooPlans){
        List<Executor> executors = Lists.newArrayList();
        bambooPlans.forEach(plan -> executors.add(createExecutor(plan)));
        return executors;
    }

}
