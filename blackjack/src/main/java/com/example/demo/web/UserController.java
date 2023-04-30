package com.example.demo.web;

import com.example.demo.entity.User;
import com.example.demo.pojo.AuthRequest;
import com.example.demo.pojo.AuthResponse;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.security.filter.JWTAuthorizationFilter;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    UserService userService;

    AuthenticationManager authenticationManager;
    JwtTokenUtil jwtTokenUtil;

    //find user id
    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id).getUsername(), HttpStatus.OK);
    }

    //find userid through username
    @GetMapping("/findbyname/{username}")
    @ResponseBody
    public Long findByUsername(@PathVariable String username) {
        return userService.getUser(username, "username").getId();
    }

    //find user credits
    @GetMapping("/{id}/credits")
    @ResponseBody
    public Integer findCredits(@PathVariable long id) {
        return userService.getCredits(userService.getUser(id));
    }


    //create a new user
    @PostMapping("/register")
    public ResponseEntity<Long> createUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        Long id = user.getId();
        //return id for the front end
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@Valid @RequestBody User request) {
//        userService.loginUser(user);
//        return new ResponseEntity<>(request.getUsername(), HttpStatus.ACCEPTED);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            User user = request;//(User)authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            //
            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //edit amount of credits
    @PutMapping("/{id}/{amount}")
    @Transactional
    public ResponseEntity<Integer> setCredits(@PathVariable long id, @PathVariable Integer amount) {
        return new ResponseEntity<>(userService.setCredits(userService.getUser(id), amount), HttpStatus.ACCEPTED);
    }
}
