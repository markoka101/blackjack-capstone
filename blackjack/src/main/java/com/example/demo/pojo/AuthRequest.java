package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String username;

    private String password;
}
