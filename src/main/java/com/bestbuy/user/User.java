package com.bestbuy.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity()
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 255)
    private String email;
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
}
