package org.trackitall.trackitall.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.trackitall.trackitall.validation.ValidRole;

@Data
public class UpdateUserRole {
    @NotBlank(message = "le champ ne doit pas etre empty.")
    @ValidRole
    private String role;
}
