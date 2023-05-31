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
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;
import static com.clarolab.model.helper.tag.TagHelper.fromBuild;
import static com.clarolab.model.types.StateType.INVALID;

@Log
@Component
public class CancelledStateProcessor extends AbstractStateProcessor implements StateProcessor {

    @Override
    public StatusType processType() {
        return StatusType.CANCELLED;
    }

    @Override
    public BuildTriage process(Build build, TriageSpec spec) {
        Tag.Builder tagBuilder = new Tag.Builder().with(AUTO_TRIAGED).with(INVALID.name()).with(fromBuild(build));

        return BuildTriage.builder()
                .lastBuild(build)
                .currentState(StateType.PERMANENT)
                .file("")
                .tags(tagBuilder.build())
                .triager(spec.getTriager())
                .triageSpec(spec)
                .rank(StateType.PERMANENT.getRankBase())
                .triaged(true)
                .build();
    }
}
