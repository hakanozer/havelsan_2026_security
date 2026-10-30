package com.works.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FieldError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getFieldErrors();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        String message = "Veri bütünlüğü hatası oluştu.";
        if (e.getMostSpecificCause().getMessage().contains("EMAIL")) {
            message = "Bu email adresi daha önce kullanılmış.";
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", message));
    }


}
