package com.example.WebQlyKho.exception;

import com.example.WebQlyKho.dto.response.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandleExceptinon {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Object> handlerCustomeException(CustomException exception){
        return APIResponse.responseBuilder(
                null,
                exception.getErrorCode().getMessage(),
                exception.getErrorCode().getCode()
                );

    }
}
