package com.task.crypto.advisor.validation.annotation;


import com.task.crypto.advisor.validation.validator.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface DateValidation {
    String message() default "Invalid date, due to format or exciting";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
