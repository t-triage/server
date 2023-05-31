package com.clarolab.jira.controller.impl;

import com.clarolab.controller.BaseController;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.jira.dto.JiraConfigDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequestMapping(API_PATH + "/configuration")
@Api(value = "jiraConfiguration", description = "Here you will find all those operations related with jiraConfiguration entities", tags = {"jiraConfiguration"})
@Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
public interface JiraConfigurationController extends BaseController<JiraConfigDTO> {

    @ApiOperation(value = "", notes = "Return a list of Jira Configuration names")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The list of products names", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")})

    @RequestMapping(value ="/javaConfiguration",  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<JiraConfigDTO> getJiraConfig(@RequestParam Long productId);


    @ApiOperation(value = "", notes = "Return a product's Jira Configuration")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The product's Jira Configuration", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")})

//    @RequestMapping(value = "/jiraConfig{productId}", method = GET, produces = APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/jiraConfig", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
//    ResponseEntity<JiraConfigDTO> findFirstByProductId(@PathVariable Long productId);
    ResponseEntity<JiraConfigDTO> findFirstByProductId(@RequestParam Long productId);


    @ApiOperation(value = "", notes = "Saves a product's Jira Configuration")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The saved Jira Configuration", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")})

    @RequestMapping(value = "/save", method = PUT, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<JiraConfigDTO> saveJiraConfig(@RequestBody JiraConfigDTO jiraConfigDTO, @RequestParam (value = "productId", required = true) Long productId);

    //ResponseEntity<T> save(@RequestBody T entity);

    //ResponseEntity<JiraConfigDTO> findFirstByProduct(@RequestParam(value = "productId", required = true) Long productId);

//    @ApiOperation(value = "", notes = "Uploads a Json file to the executor")
//    @ApiResponses(
//            value = {
//                    @ApiResponse(code = 200, message = "Tests uploaded successfully", response = List.class),
//                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
//            })
//    @RequestMapping(value = "/createNewJiraConfig",  method = POST, produces = APPLICATION_JSON_VALUE)
//    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
//    ResponseEntity<JiraConfigDTO> createNewJiraConfig(@RequestBody JiraConfigDTO entity);


}
