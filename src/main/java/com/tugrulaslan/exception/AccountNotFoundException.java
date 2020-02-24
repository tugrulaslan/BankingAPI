package com.tugrulaslan.exception;

public class AccountNotFoundException extends BusinessException {
    private static final int STATUS_CODE = 404;

    public AccountNotFoundException(String message) {
        super(STATUS_CODE, message);
    }
}
