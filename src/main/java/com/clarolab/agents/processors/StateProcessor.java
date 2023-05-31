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

import static com.clarolab.model.helper.tag.TagHelper.fromBuild;

public interface StateProcessor {

    StatusType processType();

    default BuildTriage process(Build build, TriageSpec spec) {
        Tag.Builder tagBuilder = new Tag.Builder().with(StateType.UNDEFINED.name()).with(fromBuild(build));

        return BuildTriage.builder()
                .lastBuild(build)
                .currentState(StateType.UNDEFINED)
                .file("")
                .tags(tagBuilder.build())
                .triager(spec.getTriager())
                .triageSpec(spec)
                .rank(0)
                .triaged(false)
                .build();
    }

}
