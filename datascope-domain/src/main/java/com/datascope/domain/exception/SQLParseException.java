package com.datascope.domain.exception;

/**
 * SQL解析异常
 */
public class SQLParseException extends RuntimeException {
    
    public SQLParseException(String message) {
        super(message);
    }
    
    public SQLParseException(String message, Throwable cause) {
        super(message, cause);
    }
}