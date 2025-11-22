package org.trackitall.trackitall;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.trackitall.trackitall.common.dto.UserRequestDTO;
import org.trackitall.trackitall.common.service.IUserService;

@SpringBootApplication
@RequiredArgsConstructor
public class TrackItAllApplication  {

//    private final IUserService userService;
//    private final PasswordEncoder passwordEncoder; // optionnel si utilisé dans UserService

    public static void main(String[] args) {
        SpringApplication.run(TrackItAllApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        // Test de création d’utilisateur
//        UserRequestDTO dto = new UserRequestDTO();
//        dto.setEmail("test@example.com");
//        dto.setPassword("motdepasse");
//        dto.setFirstName("amin");
//        dto.setLastName("amin");
//        dto.setRole("ADMIN"); // ou "ADMIN" selon ton enum
//
//        var user = userService.createUser(dto);
//
//        System.out.println("Utilisateur créé : " + user);
//        System.out.println("Mot de passe encodé : " + user.getPassword());
//    }
}
