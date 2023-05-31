package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.CVSRepositoryDTO;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_CVSREPOSITORY_URI)
@Api(value = "CSVRepository", description = "Here you will find all those operations related with CSVRepositories entities", tags = {"CSVRepository"})
@Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
public interface CVSRepositoryController extends BaseController<CVSRepositoryDTO>{

        @ApiOperation(value = "", notes = "Return a list of repositories url")
        @ApiResponses(
                value = {
                        @ApiResponse(code = 200, message = "The list of products names", response = List.class),
                        @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
                })
        @RequestMapping(value = NAMES,  method = GET, produces = APPLICATION_JSON_VALUE)
        @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
        ResponseEntity<List<KeyValuePair>> getCvsRepositoriesNames();

}
