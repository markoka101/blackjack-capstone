package com.example.demo.web;

import com.example.demo.entity.User;
import com.example.demo.pojo.AuthRequest;
import com.example.demo.pojo.AuthResponse;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;

@AllArgsConstructor
@RestController
@CrossOrigin(
        origins = "http://localhost:4080", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;

    //find user id
    @GetMapping("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id).getUsername(), HttpStatus.OK);
    }

    //find userid through username
    @GetMapping("/findbyname/{username}")
    @RolesAllowed("ROLE_ADMIN")
    @ResponseBody
    public Long findByUsername(@PathVariable String username) {
        return userService.getUser(username, "username").getId();
    }

    //find user credits
    @GetMapping("/{id}/credits")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
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
    public ResponseEntity<?> signIn(@Valid @RequestBody AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            User user = userService.getUser((String)authentication.getPrincipal(), "username");

            //generate token and send back
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //edit amount of credits
    @PutMapping("/{id}/{amount}")
    @RolesAllowed("ROLE_ADMIN")
    @Transactional
    public ResponseEntity<Integer> setCredits(@PathVariable long id, @PathVariable Integer amount) {
        return new ResponseEntity<>(userService.setCredits(userService.getUser(id), amount), HttpStatus.ACCEPTED);
    }
}
