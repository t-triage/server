/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.slack;

import com.clarolab.event.analytics.*;
import com.clarolab.event.process.AbstractEventHandler;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.process.EventHandler;
import com.clarolab.model.*;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.ExecutorView;
import com.clarolab.view.GroupedStatView;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.DEFAULT_SLACK_ENABLED;
import static com.clarolab.util.Constants.SLACK_ENABLED;


@Log
@Component
public class SlackNotificationEventHandler extends AbstractEventHandler implements EventHandler {


    private final int maxExecutorsToNotify = 3;

    @Autowired
    private ExecutorServiceDTO executorService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private SlackMessage slackMessage;

    @Autowired
    private SlackService slackService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ExecutorStatService executorStatService;

    @Autowired
    private ProductStatService productStatService;


    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {ApplicationEventType.TRIAGE_AGENT_EXECUTED, ApplicationEventType.TIME_NEW_DAY, ApplicationEventType.TIME_NEW_WEEK, ApplicationEventType.TIME_NEW_TUESDAY};

        if (isSlackEnabled()) {
            return handleTypes;
        } else {
            return new ApplicationEventType[]{};
        }
    }

    @Override
    public boolean process(ApplicationEvent event) {
        String pendingTitle = "Fails t-Triage";

        if (event.getType() == ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_CONTAINER) {
            return processContainer(pendingTitle, event);
        }

        if (event.getType() == ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR) {
            return processExecutor(pendingTitle, event);
        }

        if (event.getType() == ApplicationEventType.TRIAGE_AGENT_EXECUTED) {
            return processTriageAgent(pendingTitle, event);
        }

        if (event.getType() == ApplicationEventType.TIME_NEW_DAY) {
            return processSummary(event);
        }

        if (event.getType() == ApplicationEventType.TIME_NEW_WEEK) {
            sendAutomationPending();
            slackService.setSlackUserIds();
            return true;
        }

        if (event.getType() == ApplicationEventType.TIME_NEW_TUESDAY) {
            return processWeeklyContainer();
        }

        return true;
    }

    public boolean processTriageAgent(String title, ApplicationEvent event) {
        if (event.getExtraParameter() == null || event.getExtraParameter().isEmpty()) {
            // it shouldn't be null, it means no executor to process
            return false;
        }

        String buildIdWithComma = event.getExtraParameter();
        List<Long> buildIds = Arrays.stream(buildIdWithComma.split(","))
                .map(idString -> Long.valueOf(idString))
                .collect(Collectors.toList());

        List<Build> builds = buildService.findAllIds(buildIds);

        List<BuildTriage> buildTriages = buildTriageService.findWithBuilds(builds).stream()
                .filter(buildTriage -> !buildTriage.isTriaged())
                .collect(Collectors.toList());

        if (buildTriages.size() <= maxExecutorsToNotify) {
            for (BuildTriage triage : buildTriages) {
                SlackSpec slackSpec = slackSpecService.find(triage.getExecutor());
                processExecutor(title, triage, slackSpec);
            }
        } else {
            Set<Container> containers = buildTriages.stream()
                    .map(buildTriage -> buildTriage.getContainer())
                    .collect(Collectors.toSet());
            for (Container container : containers) {
                SlackSpec slackSpec = slackSpecService.find(container);
                if (slackSpec == null) {
                    return true;
                }
                if (!slackSpec.isSendDailyNotification()) {
                    processContainer(title, container, slackSpec);
                }
            }
        }

        return true;
    }

    public boolean processYesterdayTriageAgent(ApplicationEvent event) {
        String title = "Daily Summary ";

        Long since = DateUtils.beginDay(-1);
        List<ApplicationEvent> events = applicationEventService.getEventsSince(ApplicationEventType.TRIAGE_AGENT_EXECUTED, since);

        String buildIdWithComma = "";
        for (ApplicationEvent agentEvent : events) {
            if (!StringUtils.isEmpty(agentEvent.getExtraParameter())) {
                buildIdWithComma = buildIdWithComma + agentEvent.getExtraParameter() + ",";
            }
        }
        if (buildIdWithComma.length() > 1) {
            buildIdWithComma = buildIdWithComma.substring(0, buildIdWithComma.length() - 1);
        }
        List<Long> buildIds = Arrays.stream(buildIdWithComma.split(","))
                .filter(s -> !StringUtils.isEmpty(s))
                .map(idString -> Long.valueOf(idString))
                .collect(Collectors.toList());

        List<Build> builds = buildService.findAllIds(buildIds);

        List<BuildTriage> buildTriages = buildTriageService.findWithBuilds(builds).stream()
                .filter(buildTriage -> !buildTriage.isTriaged())
                .collect(Collectors.toList());

        if (buildTriages.size() <= maxExecutorsToNotify) {
            for (BuildTriage triage : buildTriages) {
                SlackSpec slackSpec = slackSpecService.find(triage.getExecutor());
                processExecutor(title, triage, slackSpec);
            }
        } else {
            Set<Container> containers = buildTriages.stream()
                    .map(buildTriage -> buildTriage.getContainer())
                    .collect(Collectors.toSet());
            for (Container container : containers) {
                SlackSpec slackSpec = slackSpecService.find(container);
                if (slackSpec == null) {
                    continue;
                }
                if (slackSpec.isSendDailyNotification()) {
                    processContainer(title, container, slackSpec);
                }
            }
        }

        return true;
    }

    public boolean processSummary(ApplicationEvent event) {
        String title = "Summary ";

        List<Container> containers = containerService.findAllEnabled();
        for (Container container : containers) {
            SlackSpec slackSpec = slackSpecService.find(container);
            if (slackSpec == null || !slackSpec.isEnabled()) {
                continue;
            }

            processDailyContainer(title, container, slackSpec);
        }

        return true;
    }

    public boolean processContainer(String mainTitle, ApplicationEvent event) {
        if (event.getExtraParameter() == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }
        Container entity = containerService.find(Long.parseLong(event.getExtraParameter()));

        if (entity == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }

        SlackSpec slackSpec = slackSpecService.find(entity);
        
        if (slackSpec == null) {
            return true;
        }

        if (!slackSpec.isSendDailyNotification()) {
            return processContainer(mainTitle, entity, slackSpec);
        }
        return true;
    }

    private boolean processContainer(String mainTitle, Container entity, SlackSpec slackSpec) {

        if (slackSpec == null || !slackSpec.isEnabled()) {
            // This is possible, the container does not have any connection to slack
            return false;
        }

        GroupedStatView view = statsService.getPendingToTriage(entity);

        if (view == null) {
            // it shouldn't be null, it means no executor view to process
            return false;
        }

        // Builds a message like: personName @ ContainerName: Tests: 140 | New Fails to triage: 15 | Fails to triage: 20 | Now Passing to triage: 3 | Done: 22.
        String message;
        // message = getRawStringMessage(view);
        message = slackMessage.getContainerMessageAttachment(mainTitle, view, entity);

        if (message == null) {
            // nothing to send
            return false;
        }

        if (slackSpec.isSendUserNotification()) {
            slackService.sendMessageNow(slackSpec, message);
            return slackService.sendMessageToUser(slackSpec, message);
        } else {
            return slackService.sendMessageNow(slackSpec, message);
        }
    }

    public boolean processExecutor(String mainTitle, ApplicationEvent event) {
        if (event.getExtraParameter() == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }
        BuildTriage buildTriage = buildTriageService.find(Long.parseLong(event.getExtraParameter()));
        if (buildTriage == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }

        SlackSpec slackSpec = slackSpecService.find(buildTriage.getExecutor());
        
        if (slackSpec == null) {
            return true;
        }

        return processExecutor(mainTitle, buildTriage, slackSpec);
    }

    private boolean processExecutor(String mainTitle, BuildTriage buildTriage, SlackSpec slackSpec) {
        Executor executor = buildTriage.getExecutor();

        if (executor == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }

        if (slackSpec == null || !slackSpec.isEnabled()) {
            // This is possible, the container does not have any connection to slack
            return false;
        }

        ExecutorView view = executorService.getExecutorViewOfLatestBuildTriage(buildTriage, buildTriage.getBuild(), executor, buildTriage.getSpec(), buildTriage.getTriager(), true);

        if (view == null) {
            // it shouldn't be null, it means no executor view to process
            return false;
        }

        // Builds a message like: personName @ ContainerName: Tests: 140 | New Fails to triage: 15 | Fails to triage: 20 | Now Passing to triage: 3 | Done: 22.
        String message;
        // message = getRawStringMessage(view);
        message = slackMessage.getSuiteMessageAttachment(mainTitle, view);

        if (message == null) {
            // nothing to send
            return false;
        }

        boolean answer = slackService.sendMessageNow(slackSpec, message);

        if (slackSpec.isSendUserNotification()) {
            return slackService.sendMessageToUser(slackSpec, message);
        }

        return answer;
    }

    public void sendAutomationPending() {
        List<SlackSpec> specs = slackSpecService.findAllEnabled();
        long previousWeek = DateUtils.beginDay(-7);

        for (SlackSpec slackSpec : specs) {
            long automationPendingThisWeek = automatedTestIssueService.countAllButFixedAfter(slackSpec.getContainer(), previousWeek);
            long automationPending = 0;
            if (automationPendingThisWeek > 0) {
                automationPending = automatedTestIssueService.countAllButFixed(slackSpec.getContainer());
            }

            long manualTestPendingThisWeek = manualTestCaseService.countAllPendingToAutomateAfter(slackSpec.getProduct(), previousWeek);
            long manualTestPending = 0;
            if (manualTestPendingThisWeek > 0) {
                manualTestPending= manualTestCaseService.countAllPendingToAutomate(slackSpec.getProduct());
            }

            if (automationPending == 0 && manualTestPending == 0) {
                // There isn't anything pending to fix. We wont spam.
            } else {
                sendAutomationPending(slackSpec, automationPending, automationPendingThisWeek, manualTestPending, manualTestPendingThisWeek);
            }
        }
    }

    private boolean sendAutomationPending(SlackSpec slackSpec, long automationPending, long automationPendingThisPeriod, long manualTestPending, long manualTestPendingThisPeriod) {
        String message = slackMessage.sendAutomationPending(slackSpec, automationPending, automationPendingThisPeriod, manualTestPending, manualTestPendingThisPeriod);
        boolean answer = slackService.sendMessageNow(slackSpec, message.toString());

        return answer;
    }

    private boolean isSlackEnabled() {
        return propertyService.valueOf(SLACK_ENABLED, DEFAULT_SLACK_ENABLED);
    }

    public boolean processDailyContainer(String mainTitle, Container entity, SlackSpec slackSpec) {

        List<BuildTriage> triages = buildTriageService.getBuildTriagedBy(entity);

        if (triages.isEmpty()) {
            return true;
        }

        boolean hasContent = false;
        StringBuffer message = new StringBuffer();

        message.append(slackMessage.getAttachmentHeader(String.format("%s Container: %s", mainTitle, entity.getName())));

        for (BuildTriage buildTriage : triages) {
            if (shouldNotify(buildTriage.getUpdated())) {
                hasContent = true;
                message.append(",");
                ExecutorView view = executorService.getExecutorViewOfLatestBuildTriage(buildTriage, buildTriage.getBuild(), buildTriage.getExecutor(), buildTriage.getSpec(), buildTriage.getTriager(), false);
                String executorMessage = slackMessage.getDailyExecutor(null, view);
                message.append(executorMessage);
            }
        }
        message.append(slackMessage.getAttachmentFooter(null));

        if (hasContent) {
            slackService.sendMessageNow(slackSpec, slackSpec.getFinalDailyChannel(), message.toString());
        }

        return true;
    }

    public boolean processWeeklyContainer() {
        Map<Container, SlackSpec> specs = slackSpecService.findAllContainers();

        specs.forEach((container, slackSpec) -> {
            processWeeklyContainer("Weekly Average Performance for: ", container, slackSpec);
        });

        return true;
    }

    public boolean processWeeklyContainer(String mainTitle, Container entity, SlackSpec slackSpec) {
        long previousWeek = DateUtils.beginDay(-12);
        long thisWeek = DateUtils.beginDay(0);

        Map<Executor,List<ExecutorStat>> executorListMap = executorStatService.findAllByContainerBetween(entity, previousWeek, thisWeek);
        StringBuffer message = new StringBuffer();

        message.append(slackMessage.getAttachmentHeader(String.format("*** _%s_ *%s* ***", mainTitle, entity.getName())));

        boolean areAllEmpty = true;
        for (Map.Entry<Executor,List<ExecutorStat>> entry : executorListMap.entrySet()) {

            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                areAllEmpty = false;
                break;
            }
        }

        if (areAllEmpty) {
            message.append(slackMessage.getWeeklyEmptyContainer(entity, slackSpec));
        } else {
            executorListMap.forEach((executor, executorStats) -> {
                EvolutionStat statSummary = executorStatService.getAgileIndex(executorStats);
                String executorMessage = slackMessage.getWeeklyExecutor(null, executor, statSummary);
                message.append(executorMessage);
            });
        }

        message.append("]");

        if (!executorListMap.isEmpty()) {
            slackService.sendMessageNow(slackSpec, slackSpec.getFinalDailyChannel(), message.toString());
        }


        return true;
    }

    public boolean processWeeklyProduct(String mainTitle, SlackSpec slackSpec) {

        //Creo un Map con Product, List<ProductStat>
        Map<Product,List<ProductStat>> productListMap = productStatService.getAllProductStat();
        StringBuffer message = new StringBuffer();

        message.append(slackMessage.getAttachmentHeader(String.format("*** _%s_ *%s* ***", mainTitle, " ")));

        boolean areAllEmpty = true;
        //Esto es para ver si está vacío
        for (Map.Entry<Product,List<ProductStat>> entry : productListMap.entrySet()) {

            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                areAllEmpty = false;
                break;
            }
        }

        if (areAllEmpty) {
            message.append(slackMessage.getWeeklyEmptyProduct( slackSpec));
        } else {
            //Recorro el map, creo un EvolutionStat (que debería crear un EvolutionProductStat)
            // y llamo para generar el mensaje mandandole el product y el EvolutionProductStat
            productListMap.forEach((product, productStats) -> {
                EvolutionProductStat statSummary = productStatService.getAgileIndex(productStats);
                String productMessage = slackMessage.getWeeklyProduct(null, product, statSummary);
                message.append(productMessage);
            });
        }

        message.append("]");


        if (!productListMap.isEmpty()) {
            slackService.sendMessageNow(slackSpec, slackSpec.getFinalDailyChannel(), message.toString());
        }


        return true;
    }
    
    private boolean shouldNotify(long timestamp) {
        return timestamp > DateUtils.beginDay(-2);
    }




}
