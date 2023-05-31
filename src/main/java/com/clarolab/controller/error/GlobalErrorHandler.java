/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.error;

import com.clarolab.aaa.exception.ResourceNotFoundException;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundServiceException.class,
            ResourceNotFoundException.class})
    public @ResponseBody
    ErrorInfo handleNotFound(HttpServletRequest req, ServiceException ex) {
        return new ErrorInfo(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestServiceException.class,
            ContainerServiceException.class,
            ConnectorServiceException.class}/*badrequestExc*/)
    public @ResponseBody
    ErrorInfo handleBadRequest(HttpServletRequest req, ServiceException ex) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidUserNameException.class/*badrequestExc*/)
    public @ResponseBody
    ErrorInfo handleUnauthorized(HttpServletRequest req, ServiceException ex) {
        return new ErrorInfo(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({
            InvalidDataException.class,
            ConfigurationError.class/*badrequestExc*/})
    public @ResponseBody
    ErrorInfo handleNotAcceptable(HttpServletRequest req, ServiceException ex) {
        return new ErrorInfo(HttpStatus.NOT_ACCEPTABLE.value(), HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            HttpServerErrorException.InternalServerError.class,
            HttpServerErrorException.class})
    public @ResponseBody
    ErrorInfo handleInternalServerError(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({
            OperationUnaceptableException.class})
    public @ResponseBody
    ErrorInfo handleOperationUnacceptableError(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(), ex.getLocalizedMessage());
    }


}
