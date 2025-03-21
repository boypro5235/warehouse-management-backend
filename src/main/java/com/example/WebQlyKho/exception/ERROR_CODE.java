package com.example.WebQlyKho.exception;

import org.springframework.http.HttpStatus;

public enum ERROR_CODE {
    USERNAME_EXISTED(HttpStatus.BAD_REQUEST, "User already in use!"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST, "Email already in use!"),
    CCCD_EXISTED(HttpStatus.BAD_REQUEST, "Cccd already in use!"),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong password!"),
    INACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "Inactive account!"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "Token expired!"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "Invalid token!"),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "Invalid signature!"),
    ;
    private final HttpStatus code;
    private final String message;

    ERROR_CODE(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpStatus getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
