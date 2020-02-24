package com.tugrulaslan.exception;

public abstract class BusinessException extends RuntimeException {
    private int statusCode;

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
