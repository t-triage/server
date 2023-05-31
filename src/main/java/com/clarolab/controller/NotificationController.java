package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.NotificationDTO;
import com.clarolab.util.Constants;
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
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequestMapping(API_NOTIFICATION_URI)
@Api(value = "Notifications",  description = "Here you will find all those operations related withNotifications entities", tags = {"Notifications"})
public interface NotificationController extends BaseController<NotificationDTO> {

    @ApiOperation(value = "", notes = "Return a list of notifications")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List of notifications", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = Constants.GET,  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<NotificationDTO>> getNotifications();

    @ApiOperation(value = "", notes = "Return a the amount of unseen notifications")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List of new notifications", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = COUNT,  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Integer> getUnseenNotificationsCount();

    @ApiOperation(value = "", notes = "Mark all notifications as seen and returns the number of updated rows")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Mark all notifications as seen", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = MARK_AS_SEEN,  method = PUT, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Integer> markNotificationsAsSeen();

}
