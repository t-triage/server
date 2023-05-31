/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.gson.deserializer;

import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIEntity;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectEntity;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CircleCIProjectDeserializer implements JsonDeserializer<CircleCIEntity> {

    @Override
    public CircleCIEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CircleCIEntity circleCIData = CircleCIEntity.builder().build();

        json.getAsJsonArray().forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            circleCIData.getCircleCIProjectDataList().add(this.getCircleCIProject(object));
        });

        return circleCIData;
    }

    private CircleCIProjectEntity getCircleCIProject(JsonObject object){
        return CircleCIProjectEntity.circleCIProjectEntityBuilder()
                .reponame(object.get("reponame").getAsString())
                .username(object.get("username").getAsString())
                .vcs_type((object.get("vcs_type").getAsString()))
                .vcs_url((object.get("vcs_url").getAsString()))
                .circleCIProjectEntityBuild();

    }
}
