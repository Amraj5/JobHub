/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;

import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.model.JobSeekerProfile;
import com.job.jobapplication.model.Skill;
import com.job.jobapplication.model.User;
import com.job.jobapplication.repository.JobSeekerProfileRepository;
import com.job.jobapplication.model.Education;
import com.job.jobapplication.repository.EducationRepository;
import com.job.jobapplication.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author ADAMS
 */

@Service
@Transactional(readOnly = true)
public class JobSeekerProfileService {
    private final JobSeekerProfileRepository profileRepository;
    private final SkillRepository skillRepository;

    public JobSeekerProfileService(JobSeekerProfileRepository profileRepository,
                                   SkillRepository skillRepository) {
        this.profileRepository = profileRepository;
        this.skillRepository = skillRepository;
    }

    /**
     * Create an empty profile during registration. Called by the auth layer.
     */
    @Transactional
    public JobSeekerProfile createEmptyProfile(User user) {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUser(user);
        profile.setTitle("Job Seeker");
        profile.setLocation("Not specified");
        return profileRepository.save(profile);
    }

    public JobSeekerProfile findByUser(User user) {
        JobSeekerProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No profile for user: " + user.getEmail()));
        profile.getSkills().size();
        profile.getEducation().size();
        return profile;
    }

    public JobSeekerProfile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + id));
    }

    /**
     * Update editable profile fields.
     */
    @Transactional
    public JobSeekerProfile updateProfile(User user, String title, String bio,
                                          String location, Integer yearsOfExperience,
                                          String cvUrl, String profilePictureUrl) {
        JobSeekerProfile profile = findByUser(user);
        if (title != null) profile.setTitle(title.trim());
        if (bio != null) profile.setBio(bio.trim());
        if (location != null) profile.setLocation(location.trim());
        if (yearsOfExperience != null) profile.setYearsOfExperience(yearsOfExperience);
        if (cvUrl != null) profile.setCvUrl(cvUrl);
        if (profilePictureUrl != null) profile.setProfilePictureUrl(profilePictureUrl);
        return profile;  // dirty checking saves
    }

    @Transactional
    public void setSkills(User user, List<String> skillNames) {
        JobSeekerProfile profile = findByUser(user);
        profile.getSkills().clear();
        for (String name : skillNames) {
            Skill skill = skillRepository.findByName(name.trim())
                    .orElseGet(() -> {
                        Skill s = new Skill();
                        s.setName(name.trim());
                        return skillRepository.save(s);
                    });
            profile.getSkills().add(skill);
        }
        // dirty checking saves
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
    
}
