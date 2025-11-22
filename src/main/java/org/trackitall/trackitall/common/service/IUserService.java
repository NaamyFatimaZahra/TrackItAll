package org.trackitall.trackitall.common.service;

import org.trackitall.trackitall.common.dto.UpdateUserRole;
import org.trackitall.trackitall.common.dto.UserRequestDTO;
import org.trackitall.trackitall.common.dto.UserResponseDTO;
import org.trackitall.trackitall.common.model.User;
import org.trackitall.trackitall.enums.UserRole;

public interface IUserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUserRole(long id, UpdateUserRole updateUserRole);
}
