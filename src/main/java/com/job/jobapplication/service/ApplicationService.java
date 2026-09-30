/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;
import com.job.jobapplication.dto.ApplicationForm;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.exception.UnauthorizedActionException;
import com.job.jobapplication.model.*;
import com.job.jobapplication.repository.ApplicationRepository;
import com.job.jobapplication.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author ADAMS
 */
@Service
@Transactional(readOnly = true)
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerProfileService profileService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              JobSeekerProfileService profileService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.profileService = profileService;
    }

    /**
     * Seeker applies to a job. Enforces business rules.
     */
    @Transactional
    public Application apply(Long jobId, ApplicationForm form, User seeker) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new BusinessRuleException("This job is no longer accepting applications.");
        }

        JobSeekerProfile profile = profileService.findByUser(seeker);

        applicationRepository.findByJobIdAndJobSeekerProfileId(jobId, profile.getId())
                .ifPresent(a -> {
                    throw new BusinessRuleException("You have already applied to this job.");
                });

        Application app = new Application();
        app.setJob(job);
        app.setJobSeekerProfile(profile);
        app.setCvUrl(form.getCvUrl());
        app.setCoverLetter(form.getCoverLetter());
        app.setStatus(ApplicationStatus.PENDING);

        return applicationRepository.save(app);
    }

    public List<Application> findBySeeker(User seeker) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.findByJobSeekerProfile(profile);
    }

    public List<Application> findRecentBySeeker(User seeker, int limit) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.findTop5ByJobSeekerProfileOrderByAppliedAtDesc(profile);
    }

    public List<Application> findByJob(Job job) {
        return applicationRepository.findByJob(job);
    }

    public List<Application> findByCompanyId(Long companyId) {
        return applicationRepository.findByJobCompanyId(companyId);
    }

    public List<Application> findRecentByCompanyId(Long companyId) {
        return applicationRepository.findTop5ByJobCompanyIdOrderByAppliedAtDesc(companyId);
    }

    public long countBySeekerAndStatus(User seeker, ApplicationStatus status) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.countByJobSeekerProfileAndStatus(profile, status);
    }

    public long countByCompanyAndStatus(Long companyId, ApplicationStatus status) {
        return applicationRepository.countByJobCompanyIdAndStatus(companyId, status);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    /**
     * Employer updates application status. Ownership enforced.
     */
    @Transactional
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus, User employer) {
        Application app = findById(applicationId);
        Job job = app.getJob();

        if (job.getCompany().getOwner() == null
                || !job.getCompany().getOwner().getId().equals(employer.getId())) {
            throw new UnauthorizedActionException("You do not own this job posting.");
        }

        app.setStatus(newStatus);
        return app;  // dirty checking saves
    }
}
