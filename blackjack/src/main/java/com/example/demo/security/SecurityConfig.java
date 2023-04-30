package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.filter.ExceptionHandlerFilter;
import com.example.demo.security.filter.JWTAuthorizationFilter;
import com.example.demo.security.filter.AuthenticationFilter;
import com.example.demo.security.filter.JwtTokenFilter;
import com.example.demo.security.manager.CustomAuthenticationManager;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
//    CustomAuthenticationManager customAuthenticationManager;

//    //testing
//    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private JwtTokenFilter jwtTokenFilter;
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(
                username -> userRepository.findByEmail(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("username not found")
                        )
        );
    }

    @Bean
    public PasswordEncoder passWordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
//        authenticationFilter.setFilterProcessesUrl("/authenticate");
        http
                .headers().frameOptions().disable()
                .and()
//                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2/**").permitAll() //remove h2 later for security reasons
                //Everyone can register or login
                .antMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.LOGIN_PATH).permitAll()

                //admin
//                .antMatchers(HttpMethod.POST).hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT).hasRole("ADMIN")
//                .antMatchers(HttpMethod.GET).hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE).hasRole("ADMIN")

                //user
//                .antMatchers(HttpMethod.GET, "/*/credits").hasRole("USER")

                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and()
                .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
//                .addFilter(authenticationFilter)
//               .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//                .exposedHeaders("Authorization");
//    }
//
//    //testing user and admin permissions
//    @Bean
//    public UserDetailsService users() {
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin-pass"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder.encode("user-pass"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

}
