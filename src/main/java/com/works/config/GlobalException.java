package com.works.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<HashMap<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return parseError(e.getFieldErrors());
    }

    private List<HashMap<String, Object>> parseError(List<FieldError> fieldErrors) {
        List<HashMap<String, Object>> errors = new java.util.ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            HashMap<String, Object> error = new HashMap<>();
            error.put("field", fieldError.getField());
            error.put("rejectedValue", fieldError.getRejectedValue());
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        }
        return errors;
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
