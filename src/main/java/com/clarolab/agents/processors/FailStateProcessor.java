/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.processors;

import com.clarolab.model.Build;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.NEED_TRIAGE;
import static com.clarolab.model.helper.tag.TagHelper.fromBuild;

@Log
@Component
public class FailStateProcessor extends AbstractStateProcessor implements StateProcessor {

    @Autowired
    TestTriageService testTriageService;

    @Override
    public StatusType processType() {
        return StatusType.FAIL;
    }

    @Override
    public BuildTriage process(Build build, TriageSpec spec) {
        StateType type = StateType.FAIL;

        Tag.Builder tagBuilder = new Tag.Builder().with(NEED_TRIAGE).with(type.name()).with(fromBuild(build));

        return BuildTriage.builder()
                .lastBuild(build)
                .currentState(type)
                .file("")
                .tags(tagBuilder.build())
                .triager(spec.getTriager())
                .triageSpec(spec)
                .rank(type.getRankBase()) //Get rank according to some kind of severities
                .triaged(false)
                .build();
    }

}
