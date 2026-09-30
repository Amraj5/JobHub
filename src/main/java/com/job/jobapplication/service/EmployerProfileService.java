/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.model.EmployerProfile;
import com.job.jobapplication.model.User;
import com.job.jobapplication.repository.EmployerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author ADAMS
 */
@Service
@Transactional(readOnly = true)
public class EmployerProfileService {
    private final EmployerProfileRepository repository;

    public EmployerProfileService(EmployerProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EmployerProfile createEmptyProfile(User user) {
        EmployerProfile profile = new EmployerProfile();
        profile.setUser(user);
        profile.setJobTitle("Recruiter");
        return repository.save(profile);
    }

    public EmployerProfile findByUser(User user) {
        return repository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No employer profile for user: " + user.getEmail()));
    }

    @Transactional
    public EmployerProfile updateProfile(User user, String jobTitle, String department,
                                         String workEmail, String linkedinUrl) {
        EmployerProfile profile = findByUser(user);
        if (jobTitle != null) profile.setJobTitle(jobTitle.trim());
        if (department != null) profile.setDepartment(department.trim());
        if (workEmail != null) profile.setWorkEmail(workEmail.trim());
        if (linkedinUrl != null) profile.setLinkedinUrl(linkedinUrl.trim());
        return profile;
    }
    
}
