package com.example.WebQlyKho.exception;

import com.example.WebQlyKho.dto.response.APIResponse;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandleExceptinon {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handlerRuntimeException(RuntimeException exception){
        return APIResponse.responseBuilder(
                null,
                exception.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Object> handlerCustomeException(CustomException exception){
        return APIResponse.responseBuilder(
                null,
                exception.getErrorCode().getMessage(),
                exception.getErrorCode().getCode()
                );

    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        return APIResponse.responseBuilder(
                null,
                exception.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST
                );
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    public ResponseEntity<Object> handlerMalformedJwtException(MalformedJwtException exception){
        return APIResponse.responseBuilder(
                null,
                exception.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST
                );
    }
}
