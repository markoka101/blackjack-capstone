package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class RolesServiceImpl implements RolesService{

    private RoleRepository roleRepository;
    private UserRepository userRepository;

    @Override
    public Set<Role> getRoles() {
        return null;
    }

    @Override
    public void setRoles(Set<Role> roles) {

    }

    @Override
    public void addRole(Role role) {

    }

}
