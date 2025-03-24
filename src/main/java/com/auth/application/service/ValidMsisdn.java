package com.auth.application.service;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MsisdnValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMsisdn {

    String message() default "Invalid MSISDN format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
