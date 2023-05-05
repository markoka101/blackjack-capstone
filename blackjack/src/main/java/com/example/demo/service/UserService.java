package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
    User getUser(Long id);
    User getUser(String usernameOrEmail, String option);
    User saveUser(User user);
    User loginUser(User user);
    Integer getCredits(User user);
    Integer setCredits(User user, Integer credits);

}
