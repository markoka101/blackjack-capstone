package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "USERS")

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message =  "username cannot be blank")
    @NonNull
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message =  "password cannot be blank")
    @NonNull
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column()
    private Integer credits;
}
