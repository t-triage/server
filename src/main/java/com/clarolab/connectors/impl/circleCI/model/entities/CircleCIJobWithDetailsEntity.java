/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.entities;

import com.clarolab.connectors.impl.circleCI.model.types.CircleCIStatusType;
import com.clarolab.model.Build;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
public class CircleCIJobWithDetailsEntity extends CircleCIProjectEntity {

    private String job_name;
    private int build_num;
    private boolean has_artifacts;
    private long build_time_millis;
    private CircleCIStatusType status;
    private String build_url;
    private boolean failed;
    private String start_time;
    private String stop_time;
    private CircleCIProjectBuildParametersEntity build_parameters;
    private CircleCIProjectBuildArtifactEntity circleCIProjectBuildArtifactEntity;

    @Builder(builderMethodName = "circleCIJobWithDetailsEntityBuilder", buildMethodName = "circleCIJobWithDetailsEntityBuild")
    public CircleCIJobWithDetailsEntity(String reponame, String username, String vcs_type, String vcs_url, String job_name, int build_num, boolean has_artifacts, long build_time_millis, CircleCIStatusType status, String build_url, boolean failed, String start_time, String stop_time, CircleCIProjectBuildParametersEntity build_parameters, CircleCIProjectBuildArtifactEntity circleCIProjectBuildArtifactEntity){
        super(reponame, username, vcs_type, vcs_url);
        this.job_name = job_name;
        this.build_num = build_num;
        this.has_artifacts = has_artifacts;
        this.build_time_millis = build_time_millis;
        this.status = status;
        this.build_url = build_url;
        this.failed = failed;
        this.start_time = start_time;
        this.stop_time = stop_time;
        this.build_parameters = build_parameters;
        this.circleCIProjectBuildArtifactEntity = circleCIProjectBuildArtifactEntity;
    }

    public Build getBuild(){
        return Build.builder()
                .number(this.getBuild_num())
                .buildId(this.getBuild_parameters().getCIRCLE_JOB()+"#"+this.getBuild_num())
                .displayName(this.getBuild_parameters().getCIRCLE_JOB()+"#"+this.getBuild_num())
                .url(this.build_url)
                .executedDate(DateUtils.convertDate(this.getStart_time()))
                .status(this.getBuildStatus())
                .report(null)
                .enabled(true)
                .populateMode(PopulateMode.PULL)
                .timestamp(DateUtils.now())
                .build();
    }

    public String getJobName(){
        return !Strings.isNullOrEmpty(this.job_name) ? this.job_name : this.getBuild_parameters().getCIRCLE_JOB();
    }

    public boolean isValidJob(){
        return this.job_name != null ||  (this.getBuild_parameters() != null && this.getBuild_parameters().getCIRCLE_JOB() != null);
    }

    public CircleCIProjectBuildArtifactElementEntity getArtifactWithReport(){
        return circleCIProjectBuildArtifactEntity.getArtifactWithTestReport();
    }

    public CircleCIProjectBuildArtifactElementEntity getArtifactWithApplicationTestingEnvironmentVersion(){
        return circleCIProjectBuildArtifactEntity.getArtifactWithApplicationTestingEnvironmentVersion();
    }

    public StatusType getBuildStatus(){
        switch (status){
            case SUCCESS:
                return StatusType.SUCCESS;
            case FAILED:
                return StatusType.FAIL;
            case RETRIED:
                return StatusType.REBUILDING;
            case IT_FAIL:
                return StatusType.INFRASTRUCTURE_FAIL;
            case CANCELED:
                return StatusType.CANCELLED;
            case TIME_OUT:
                return StatusType.TIME_OUT;
            case RUNNING:
                return StatusType.BUILDING;
            case QUEUED:
                return StatusType.QUEUED;
            case SCHEDULED:
                return StatusType.SCHEDULED;
            case NOT_RUNNING:
                return StatusType.NOT_BUILT;
            case NO_TESTS:
                return StatusType.NO_TESTS;
            case FIXED:
                return StatusType.FIXED;
            default:
                return StatusType.UNKNOWN;
        }
    }

}
