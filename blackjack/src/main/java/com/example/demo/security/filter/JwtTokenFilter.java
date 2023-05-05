package com.example.demo.security.filter;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!hasAuthBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);
        if (!jwtTokenUtil.validateAccessToken(token)) {
            filterChain.doFilter(request,response);
            return;
        }

        setAuthContext(token, request);
        filterChain.doFilter(request,response);
    }

    private boolean hasAuthBearer(HttpServletRequest  request) {
        String header = request.getHeader("Authorization");

        if (ObjectUtils.isEmpty(header) || !header.startsWith(SecurityConstants.BEARER)) {
            return false;
        }

        return true;
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    private void setAuthContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    //extract the user info from the token
    private UserDetails getUserDetails(String token) {
        User userDetails = new User();
        Claims claims = jwtTokenUtil.parseClaims(token);
        String roles = (String) claims.get("roles");


        roles = roles.replace("[", "").replace("]", "");
        String[] rolesNames = roles.split(",");

        for (String aRoleName : rolesNames) {
            userDetails.addRole(new Role(aRoleName));
        }

        String[] jwtSubject = jwtTokenUtil.getSubject(token).split(",");

        userDetails.setId(Long.parseLong(jwtSubject[0]));
        userDetails.setUsername(jwtSubject[1]);

        return userDetails;
    }
}
