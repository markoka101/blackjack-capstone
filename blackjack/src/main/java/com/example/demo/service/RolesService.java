package com.example.demo.service;


import com.example.demo.entity.Role;

import java.util.Set;

public interface RolesService {
    Set<Role> getRoles();
    void setRoles(Set<Role> roles);
    void addRole (Role role);
}
