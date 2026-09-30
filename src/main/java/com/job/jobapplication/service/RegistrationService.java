/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;
import com.job.jobapplication.dto.RegisterRequest;
import com.job.jobapplication.model.Role;
import com.job.jobapplication.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author ADAMS
 */
@Service
public class RegistrationService {
    private final UserService userService;
    private final JobSeekerProfileService seekerProfileService;
    private final EmployerProfileService employerProfileService;

    public RegistrationService(UserService userService,
                               JobSeekerProfileService seekerProfileService,
                               EmployerProfileService employerProfileService) {
        this.userService = userService;
        this.seekerProfileService = seekerProfileService;
        this.employerProfileService = employerProfileService;
    }

    /**
     * One transaction, one business operation.
     * If any step fails, everything rolls back.
     */
    @Transactional
    public User register(RegisterRequest request) {
        User user = userService.register(request);

        if (user.getRole() == Role.JOB_SEEKER) {
            seekerProfileService.createEmptyProfile(user);
        } else if (user.getRole() == Role.EMPLOYER) {
            employerProfileService.createEmptyProfile(user);
        }

        return user;
    }
}
