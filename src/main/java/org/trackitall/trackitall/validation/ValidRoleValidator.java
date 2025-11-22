package org.trackitall.trackitall.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.trackitall.trackitall.enums.UserRole;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        if (role == null) return false;

        try {
            UserRole.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
