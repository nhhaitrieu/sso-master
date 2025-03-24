package com.auth.application.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MsisdnValidator implements ConstraintValidator<ValidMsisdn, String> {
    @Override
    public void initialize(ValidMsisdn constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String msisdn, ConstraintValidatorContext context) {
        if (msisdn == null || msisdn.isEmpty()) {
            return false;
        }
        // Regex cho số Việt Nam (+84 hoặc 0 và 9 số)
        String vnRegex = "^(\\+84|0)[0-9]{9}$";
        // Regex cho số quốc tế (+ mã quốc gia và số)
        String intlRegex = "^\\+[1-9][0-9]{1,3}[0-9]{6,12}$";

        return msisdn.matches(vnRegex) || msisdn.matches(intlRegex);    }
}
