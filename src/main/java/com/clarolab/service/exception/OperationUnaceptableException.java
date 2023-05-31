package com.clarolab.service.exception;

public class OperationUnaceptableException extends ServiceException {

    public OperationUnaceptableException(String message) {
        super(message);
    }

    public OperationUnaceptableException(Exception e) {
        super(e);
    }
}
