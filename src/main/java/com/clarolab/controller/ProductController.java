/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ProductDTO;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequestMapping(API_PRODUCT_URI)
@Api(value = "Product", description = "Here you will find all those operations related with Product entities", tags = {"Product"})
@Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
public interface ProductController extends BaseController<ProductDTO> {

    @ApiOperation(value = "", notes = "Return a list of product names")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The list of products names", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = NAMES,  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<KeyValuePair>> getProductNames();

    @ApiOperation(value = "", notes = "Update packageNames and logPattern of Product")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Entry updated successfully.", response = ProductDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = "/updateFields", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<ProductDTO> update(@RequestParam Long id, @RequestParam(required = false) String packageNames, @RequestParam(required = false) String logPattern);

    @ApiOperation(value = "", notes = "Returns the amount of products")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Size of the product list", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/list/size",  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<Long> getProductAmount();
}
