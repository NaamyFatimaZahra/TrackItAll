package org.trackitall.trackitall.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.trackitall.trackitall.enums.UserRole;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
}
