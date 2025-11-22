    package org.trackitall.trackitall.common.model;

    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.trackitall.trackitall.enums.UserRole;

    import java.util.Collection;
    import java.util.List;

    @Entity
    @Table(name="users")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public class User  {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column(nullable = false)
        private String firstName;
        @Column(nullable = false)
        private String lastName;
        @Column(nullable = false,unique = true)
        private String email;
        @Column(nullable = false)
        private String password;
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserRole role;

    }
