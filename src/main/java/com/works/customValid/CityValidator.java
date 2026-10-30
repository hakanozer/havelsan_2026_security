package com.works.customValid;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CityValidator implements ConstraintValidator<CityValid,String> {

    String[] cities = {"İstanbul", "Tekirdağ", "Bursa"};

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        for (String city : cities) {
            if (city.equals(s)) {
                return true;
            }
        }
        return false;
    }

}
