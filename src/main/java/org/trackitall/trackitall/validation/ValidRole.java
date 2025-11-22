package org.trackitall.trackitall.validation;
import jakarta.validation.Constraint;

import jakarta.validation.Payload;
import org.trackitall.trackitall.validation.ValidRoleValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRoleValidator.class)
public @interface ValidRole{
      String message() default "Rôle invalide. Veuillez choisir un role parmi ceux autorises.";
      Class <?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
}
