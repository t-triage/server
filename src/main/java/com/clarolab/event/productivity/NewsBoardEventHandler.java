package com.clarolab.event.productivity;

import com.clarolab.event.process.AbstractEventHandler;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.process.EventHandler;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.User;
import com.clarolab.model.helper.NewsBoardHelper;
import com.clarolab.model.types.StateType;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.NewsBoardService;
import com.clarolab.service.TestTriageService;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Log
@Component
public class NewsBoardEventHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    private NewsBoardService newsBoardService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {
                ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR,
                ApplicationEventType.BUILD_TRIAGED,
                ApplicationEventType.AUTOMATION_TEST_CHANGED,
                ApplicationEventType.TIME_NEW_DAY
        };

        return handleTypes;

    }

    @Override
    public boolean process(ApplicationEvent event) {

        switch (event.getType()) {
            case AUTOMATION_TEST_CHANGED:
                automationChanged(event);
                break;
            case BUILD_TRIAGED:
                buildTriaged(event);
                break;
            case BUILD_TRIAGE_GENERATED_FOR_EXECUTOR:
                newBuildProcessed(event);
                break;
            case TIME_NEW_DAY:
                processYesterday(event);
                break;
            default:
        }

        return true;
    }

    public void newBuildProcessed(ApplicationEvent event) {
        if (event.getSource() == null) {
            // it shouldn't be null, it means no executor to process
            return;
        }

        BuildTriage buildTriage = null;
        try {
            buildTriage = buildTriageService.find(event.getSource().getId());
            if (buildTriage == null) {
                log.log(Level.WARNING, String.format("Couldn't find associated BuildTriage in event %d", event.getId()));
            }
        } catch (JDBCException ex) {
            log.log(Level.SEVERE, String.format("Error unproxing Event", event.getId()), ex);
        }

        if (buildTriage == null) {
            // it shouldn't be null, it means no executor to process
            return;
        }

        long fails = testTriageService.countByStateAndTriaged(buildTriage.getBuild(), StateType.FAIL, false);
        newsBoardService.create(NewsBoardHelper.buildCreated(buildTriage, fails), event.getEventTime(), event.getType());

    }

    public void buildTriaged(ApplicationEvent event) {
        if (event.getExtraParameter() == null) {
            // it shouldn't be null, it means no executor to process
            return;
        }

        BuildTriage buildTriage = buildTriageService.find(Long.parseLong(event.getExtraParameter()));

        if (buildTriage == null) {
            // it shouldn't be null, it means no executor to process
            return;
        }

        newsBoardService.create(NewsBoardHelper.buildTriaged(buildTriage), event.getEventTime(), event.getType());

    }

    public void automationChanged(ApplicationEvent event) {
        newsBoardService.create(event.getProcessingMessage(), event.getEventTime(), event.getType());
    }

    public void processYesterday(ApplicationEvent event) {

        long totalToTriage = testTriageService.countPendingToTriage();

        if (totalToTriage > 0) {
            newsBoardService.create(NewsBoardHelper.yesterdayTotalPending(totalToTriage), event.getEventTime(), event.getType());
        }

        User user = testTriageService.getBestTriager(DateUtils.beginDay(-1), DateUtils.beginDay(0));
        if (user != null) {
            newsBoardService.create(NewsBoardHelper.yesterdayTriaged(user.getDisplayName()), event.getEventTime(), event.getType());
        }
    }

}
