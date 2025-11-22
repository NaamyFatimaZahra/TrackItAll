package org.trackitall.trackitall.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trackitall.trackitall.common.dto.UpdateUserRole;
import org.trackitall.trackitall.common.dto.UserRequestDTO;
import org.trackitall.trackitall.common.dto.UserResponseDTO;
import org.trackitall.trackitall.common.mapper.UserMapper;
import org.trackitall.trackitall.common.model.User;
import org.trackitall.trackitall.common.repository.UserRepository;
import org.trackitall.trackitall.enums.UserRole;
import org.trackitall.trackitall.exception.BusinessException;
import org.trackitall.trackitall.exception.NotFoundException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper usermapper;
    private final PasswordEncoder passwordEncoder;



    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user=usermapper.toEntity(userRequestDTO);
        userRepository.findUserByEmail(user.getEmail()).ifPresent(
                user1 -> {throw new BusinessException("Email deja exist.");
                }
        );
        String passwordHashed=passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHashed);

        User saveduser=userRepository.save(user);
       return usermapper.toResponseDTO(saveduser);
    }


    @Override
    @Transactional
    public UserResponseDTO updateUserRole(long id, UpdateUserRole updateUserRole){
        User user=userRepository.findUserById(id).orElseThrow(()->{throw new NotFoundException("user avec id: "+id+" n'exist pas.");
        });
        UserRole newRole= usermapper.toUserRole(updateUserRole);

        user.setRole(newRole);

        return usermapper.toResponseDTO(user);
    }

}
