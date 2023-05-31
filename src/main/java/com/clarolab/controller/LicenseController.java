package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.LicenceStatusDTO;
import com.clarolab.dto.LicenseDTO;
import com.clarolab.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_LICENSE_URI)
@Api(value = "License", description = "Here you will find all those operations related with the License", tags = {"License"})
@Secured(value =  ROLE_ADMIN)
public interface LicenseController extends  BaseController<LicenseDTO> {

    @ApiOperation(value = "", notes = "Get License data")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Get", response = LicenseDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = Constants.GET, method = GET, produces = APPLICATION_JSON_VALUE )
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<LicenseDTO> getLicense();

    @ApiOperation(value = "", notes = "Get License status info")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "State", response = LicenceStatusDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATUS, method = GET, produces = APPLICATION_JSON_VALUE )
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<LicenceStatusDTO> getLicenseStatus();

    @ApiOperation(value = "", notes = "Check if the license has expired")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "License expiration info", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = CHECK , method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkLicenceExpiry();

}
