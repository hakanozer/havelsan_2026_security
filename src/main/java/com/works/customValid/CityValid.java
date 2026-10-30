package com.works.customValid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CityValidator.class)
public @interface CityValid {
    String message() default "Invalid city";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
