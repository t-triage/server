/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.gson.deserializer;

import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactElementEntity;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactEntity;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CircleCIProjectBuildArtifactDeserializer implements JsonDeserializer<CircleCIProjectBuildArtifactEntity> {

    @Override
    public CircleCIProjectBuildArtifactEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CircleCIProjectBuildArtifactEntity circleCIProjectBuildArtifactEntity = CircleCIProjectBuildArtifactEntity.builder().build();

        json.getAsJsonArray().forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            circleCIProjectBuildArtifactEntity.getCircleCIProjectBuildArtifactElementEntities().add(this.getArtifact(object));
        });
        return circleCIProjectBuildArtifactEntity;
    }

    private CircleCIProjectBuildArtifactElementEntity getArtifact(JsonObject jsonObject){
        return CircleCIProjectBuildArtifactElementEntity.builder()
                .path(jsonObject.get("path").getAsString())
                .pretty_path(jsonObject.get("pretty_path").getAsString())
                .url(jsonObject.get("url").getAsString())
                .node_index(jsonObject.get("node_index").getAsInt())
                .build();
    }
}
