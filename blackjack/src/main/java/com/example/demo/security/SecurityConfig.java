package com.example.demo.security;

import com.example.demo.security.filter.ExceptionHandlerFilter;
import com.example.demo.security.filter.JWTAuthorizationFilter;
import com.example.demo.security.filter.AuthenticationFilter;
import com.example.demo.security.manager.CustomAuthenticationManager;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {
    private final CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
        authenticationFilter.setFilterProcessesUrl("/authenticate");
        http
                .headers().frameOptions().disable()
                .and()
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2/**").permitAll() //remove h2 later for security reasons
//                .antMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
//                .antMatchers(HttpMethod.POST, SecurityConstants.LOGIN_PATH).permitAll()
                .antMatchers(HttpMethod.POST).permitAll()
                .antMatchers(HttpMethod.PUT).permitAll()
                .antMatchers(HttpMethod.GET).permitAll()//testing
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .exposedHeaders("Authorization");
    }

}
