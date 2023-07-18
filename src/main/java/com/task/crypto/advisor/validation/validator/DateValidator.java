package com.task.crypto.advisor.validation.validator;

import com.task.crypto.advisor.validation.annotation.DateValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<DateValidation, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
             if (LocalDate.parse(s,DateTimeFormatter.ISO_DATE).isBefore(LocalDate.now())) return true;
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }
}
