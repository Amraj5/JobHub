package com.job.jobapplication.service;

import com.job.jobapplication.dto.ApplicationForm;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.exception.UnauthorizedActionException;
import com.job.jobapplication.model.Application;
import com.job.jobapplication.model.ApplicationStatus;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.JobSeekerProfile;
import com.job.jobapplication.model.JobStatus;
import com.job.jobapplication.model.User;
import com.job.jobapplication.repository.ApplicationRepository;
import com.job.jobapplication.repository.JobRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // ============ SEEKER: APPLY ============
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

    // ============ SEEKER: LIST / COUNT ============
    public List<Application> findBySeeker(User seeker) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.findBySeekerWithDetails(profile);
    }

    public List<Application> findTop5BySeeker(User seeker) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.findRecentBySeekerWithDetails(
                profile, PageRequest.of(0, 5));
    }

    public long countBySeeker(User seeker, ApplicationStatus status) {
        JobSeekerProfile profile = profileService.findByUser(seeker);
        return applicationRepository.countByJobSeekerProfileAndStatus(profile, status);
    }

    // ============ EMPLOYER: LIST / COUNT ============
    public List<Application> findByJob(Job job) {
        return applicationRepository.findByJobWithDetails(job);
    }

    public List<Application> findByCompanyId(Long companyId) {
        return applicationRepository.findByCompanyWithDetails(companyId);
    }

    public List<Application> findRecentByCompanyId(Long companyId) {
        return applicationRepository.findRecentByCompanyWithDetails(
                companyId, PageRequest.of(0, 5));
    }

    public long countByCompanyAndStatus(Long companyId, ApplicationStatus status) {
        return applicationRepository.countByJobCompanyIdAndStatus(companyId, status);
    }

    // ============ SHARED ============
    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }
    
    public long countAll() {
        return applicationRepository.count();
    }

    public List<Application> findAll() {
        return applicationRepository.findAllWithDetails();
    }

    @Transactional
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus, User employer) {
        Application app = findById(applicationId);
        Job job = app.getJob();

        if (job.getCompany().getOwner() == null
                || !job.getCompany().getOwner().getId().equals(employer.getId())) {
            throw new UnauthorizedActionException("You do not own this job posting.");
        }

        app.setStatus(newStatus);
        return app;
    }
}