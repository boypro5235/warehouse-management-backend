package com.example.WebQlyKho.exception;

import org.springframework.http.HttpStatus;

public enum ERROR_CODE {

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
