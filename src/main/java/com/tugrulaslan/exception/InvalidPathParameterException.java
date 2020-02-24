package com.tugrulaslan.exception;

public class InvalidPathParameterException extends BusinessException {
    private static final int STATUS_CODE = 400;

    public InvalidPathParameterException(String message) {
        super(STATUS_CODE, message);
    }
}
