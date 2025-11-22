package org.trackitall.trackitall.common.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.trackitall.trackitall.common.dto.UpdateUserRole;
import org.trackitall.trackitall.common.dto.UserRequestDTO;
import org.trackitall.trackitall.common.dto.UserResponseDTO;
import org.trackitall.trackitall.common.repository.UserRepository;
import org.trackitall.trackitall.common.service.IUserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO>createUser(@Valid UserRequestDTO userRequestDTO){
        UserResponseDTO createdUser=userService.createUser(userRequestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable long id,
            @RequestBody @Valid UpdateUserRole request ){


        UserResponseDTO updatedUser=userService.updateUserRole(id,request);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

}
