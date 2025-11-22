package org.trackitall.trackitall.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.trackitall.trackitall.enums.UserRole;
import org.trackitall.trackitall.validation.ValidRole;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "First Name ne doit pas etre empty")
    private String firstName;
    @NotBlank(message = "Last Name ne doit pas etre empty")
    private String lastName;
    @NotBlank(message = "Email ne doit pas etre empty")
    private String email;
    @NotBlank(message = "password ne doit pas etre empty")
    private String password;
    @NotNull(message = "le role ne doit pas etre empty")
    @ValidRole
    private String role;


}
