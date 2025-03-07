package com.example.WebQlyKho.exception;

public class CustomException extends RuntimeException {
    private ERROR_CODE errorCode;

    public CustomException(ERROR_CODE errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ERROR_CODE getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ERROR_CODE errorCode) {
        this.errorCode = errorCode;
    }
}
