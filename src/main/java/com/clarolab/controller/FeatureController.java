/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.view.feature.FeatureListView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

@Api(value = "Features", description = "Here you will find all enabled/disabled application features", tags = {"Features"})
public interface FeatureController extends SecuredController{

    @ApiOperation(value = "", notes = "Return a Feature list")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Feature list returned successfully.", response = FeatureListView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    ResponseEntity<FeatureListView> getFeatures();

}
