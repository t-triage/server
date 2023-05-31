/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ProductComponentDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_PRODUCT_COMPONENT_URI)
@Api(value = "ProductComponent", description = "Here you will find all the operations related with ProductComponent entities", tags = {"ProductComponent"})
public interface ProductComponentController extends BaseController<ProductComponentDTO> {

    @ApiOperation(value = "", notes = "Search Product Component by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Product Component search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<ProductComponentDTO>> search(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "default", required = false) boolean defaultComponents1);

    @ApiOperation(value = "", notes = "Search Product Component by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Product Component search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<ProductComponentDTO>> suggested(@RequestParam(value = "component1", required = false) Long component1, @RequestParam(value = "component2", required = false) Long component2);

}
