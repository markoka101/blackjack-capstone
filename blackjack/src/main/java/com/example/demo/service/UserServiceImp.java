package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return unwrapUser(user, id);
    }

    @Override
    public User getUser(String usernameOrEmail, String option) {
        Optional<User> user;
        if (option.equals("username")) {
            user = userRepository.findByUsername(usernameOrEmail);
        } else {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return unwrapUser(user, 404L);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User loginUser(User user) {
        User savedUser = (getUser(user.getUsername(), "username"));
        if (bCryptPasswordEncoder.matches(user.getPassword(), savedUser.getPassword())) {
            System.out.println("login successful");
            return user;
        }
        return null;
    }

    @Override
    public Integer getCredits(User user) {
        return user.getCredits();
    }

    @Override
    public Integer setCredits(User user, Integer credits) {
        user.setCredits(credits);
        return user.getCredits();
    }
    static User unwrapUser(Optional<User> entity, Long id) {

        if  (entity.isPresent()) {
            return entity.get();
        }
        return null;//get rid of when make exception
        //else throw new EntityNotFoundException(id, User.class);*/
    }
}
