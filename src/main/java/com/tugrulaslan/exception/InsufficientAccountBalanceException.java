package com.tugrulaslan.exception;

public class InsufficientAccountBalanceException extends BusinessException {
    private static final int STATUS_CODE = 400;

    public InsufficientAccountBalanceException(String message) {
        super(STATUS_CODE, message);
    }
}
