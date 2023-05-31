/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Log
public class MainRobot {

    private RobotReport robot;
    private ApplicationContextService context;

    public boolean isEmpty() {
        return robot == null;
    }

    public int getPassCount() {
        return robot.getPassed();
    }

    public int getFailCount() {
        return robot.getFailed();
    }

    public int getSkipCount() {
        return robot.getSkipped();
    }

    public Long getExecutedDate() {
        return robot.getGeneratedDate();
    }

    public Long getDuration() {
        return robot.getDuration();
    }

    public StatusType getStatus() {
        return robot.getStatus();
    }

    public List<TestExecution> getTests() {
        robot.setContext(context);
        return robot.getTests();
    }

}
