/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.gson.deserializer;

import com.clarolab.connectors.impl.circleCI.CircleCIApiEndpoints;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIJobEntity;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIJobWithDetailsEntity;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactEntity;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildParametersEntity;
import com.clarolab.connectors.impl.circleCI.model.types.CircleCIStatusType;
import com.clarolab.http.client.HttpClient;
import com.clarolab.util.Constants;
import com.clarolab.util.JsonUtils;
import com.google.gson.*;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.logging.Level;

@Log
public class CircleCIJobDeserializer implements JsonDeserializer<CircleCIJobEntity> {

    private GsonBuilder gsonBuilder;
    private HttpClient httpclient;
    private boolean isPlainJob = false;

    public CircleCIJobDeserializer(HttpClient httpclient, boolean isPlainJob){
        this(httpclient);
        this.isPlainJob = isPlainJob;
    }
    public CircleCIJobDeserializer(HttpClient httpclient){
        this.httpclient = httpclient;
        gsonBuilder = new GsonBuilder().registerTypeAdapter(CircleCIProjectBuildArtifactEntity.class, new CircleCIProjectBuildArtifactDeserializer());
    }

    @Override
    public CircleCIJobEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializeAll(json, typeOfT, context);
        // return deserializeLimit(json, typeOfT, context);
    }

    public CircleCIJobEntity deserializeAll(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CircleCIJobEntity circleCIJobEntity = CircleCIJobEntity.builder().build();

        json.getAsJsonArray().forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            if(!this.isPlainJob)
                circleCIJobEntity.getCircleCIJobWithDetailsEntityList().add(this.getCircleCIJob(object));
            else
                circleCIJobEntity.getCircleCIJobWithDetailsEntityList().add(this.getCircleCIPlainJob(object));
        });

        return circleCIJobEntity;
    }

    public CircleCIJobEntity deserializeLimit(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CircleCIJobEntity circleCIJobEntity = CircleCIJobEntity.builder().build();

        JsonArray array = json.getAsJsonArray();
        int max = Math.min(array.size(), Constants.DEFAULT_MAX_BUILDS_TO_PROCESS);
        for (int i = 0; i < max; i++) {
            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();
            if(!this.isPlainJob)
                circleCIJobEntity.getCircleCIJobWithDetailsEntityList().add(this.getCircleCIJob(object));
            else
                circleCIJobEntity.getCircleCIJobWithDetailsEntityList().add(this.getCircleCIPlainJob(object));
        }

        return circleCIJobEntity;
    }

    private CircleCIJobWithDetailsEntity getCircleCIPlainJob(JsonObject object){
        return CircleCIJobWithDetailsEntity.circleCIJobWithDetailsEntityBuilder()
                .job_name(JsonUtils.parseString(object, "job_name", null))
                .build_parameters(CircleCIProjectBuildParametersEntity.builder().CIRCLE_JOB(JsonUtils.parseObjectAsString(object, "build_parameters", "CIRCLE_JOB", null)).build())
                .build_num((int) JsonUtils.parseLong(object, "build_num", 0))
                .circleCIJobWithDetailsEntityBuild();
    }

    private CircleCIJobWithDetailsEntity getCircleCIJob(JsonObject object){
        CircleCIJobWithDetailsEntity circleCIJobWithDetailsEntity = CircleCIJobWithDetailsEntity.circleCIJobWithDetailsEntityBuilder()
                .reponame(JsonUtils.parseString(object, "reponame", null))
                .username(JsonUtils.parseString(object, "username", null))
                .vcs_type((JsonUtils.parseString(object, "vcs_type", null)))
                .vcs_url((JsonUtils.parseString(object, "vcs_url", null)))
                .job_name(JsonUtils.parseString(object, "job_name", null))
                .build_num((int) JsonUtils.parseLong(object, "build_num", 0))
                .has_artifacts(JsonUtils.parseBoolean(object, "has_artifacts", false))
                .build_time_millis(JsonUtils.parseLong(object, "build_time_millis", 0))
                .status(CircleCIStatusType.getStatus(JsonUtils.parseString(object, "status", "UNKNOWN")))
                .build_url(JsonUtils.parseString(object, "build_url", null))
                .failed(JsonUtils.parseBoolean(object, "failed", true))
                .start_time(JsonUtils.parseString(object, "start_time", null))
                .stop_time(JsonUtils.parseString(object, "stop_time", null))
                .build_parameters(CircleCIProjectBuildParametersEntity.builder().CIRCLE_JOB(JsonUtils.parseObjectAsString(object, "build_parameters", "CIRCLE_JOB", null)).build())
                .circleCIJobWithDetailsEntityBuild();
        try {
            circleCIJobWithDetailsEntity.setCircleCIProjectBuildArtifactEntity(gsonBuilder.create().fromJson(httpclient.get(String.format(CircleCIApiEndpoints.BUILD_ARTIFACT, circleCIJobWithDetailsEntity.getVcs_type(), circleCIJobWithDetailsEntity.getUsername(), circleCIJobWithDetailsEntity.getReponame(), circleCIJobWithDetailsEntity.getBuild_num())), CircleCIProjectBuildArtifactEntity.class));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error getting a getCircleCIJob", e);
        }

        return circleCIJobWithDetailsEntity;
    }


}
