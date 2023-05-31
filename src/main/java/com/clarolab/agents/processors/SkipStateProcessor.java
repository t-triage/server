/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.processors;

import com.clarolab.model.Build;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;
import static com.clarolab.model.helper.tag.TagHelper.fromBuild;
import static com.clarolab.model.types.StateType.PERMANENT;

@Log
@Component
public class SkipStateProcessor extends AbstractStateProcessor implements StateProcessor {

    @Autowired
    TestTriageService testTriageService;

    @Override
    public StatusType processType() {
        return StatusType.SKIP;
    }

    @Override
    public BuildTriage process(Build build, TriageSpec spec) {
        Tag.Builder tagBuilder = new Tag.Builder().with(AUTO_TRIAGED).with(PERMANENT.name()).with(fromBuild(build));

        return BuildTriage.builder()
                .lastBuild(build)
                .currentState(PERMANENT)
                .file("")
                .tags(tagBuilder.build())
                .triager(spec.getTriager())
                .triageSpec(spec)
                .rank(PERMANENT.getRankBase()) //Get rank according to some kind of severities
                .triaged(true)
                .build();
    }

}
