/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;
import com.job.jobapplication.dto.RegisterRequest;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.model.Role;
import com.job.jobapplication.model.User;
import com.job.jobapplication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author ADAMS
 */

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public User register(RegisterRequest req) {
        if (req.getRole() == Role.ADMIN) {
            throw new BusinessRuleException("Cannot register as admin.");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessRuleException("Email is already registered.");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessRuleException("Username is already taken.");
        }

        User user = new User();
        user.setEmail(req.getEmail().trim().toLowerCase());
        user.setUsername(req.getUsername().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName().trim());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        user.setActive(true);

        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("No user with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No user with id: " + id));
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void toggleActive(Long userId) {
        User user = findById(userId);
        user.setActive(!user.isActive());
    }
}
