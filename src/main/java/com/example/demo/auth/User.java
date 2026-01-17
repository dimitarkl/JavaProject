package com.example.demo.auth;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name="first_name",nullable = false)
    String firstName;

    @Column(name="last_name",nullable = false)
    String lastName;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}