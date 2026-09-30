package com.job.jobapplication.repository;

import com.job.jobapplication.model.Application;
import com.job.jobapplication.model.ApplicationStatus;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobSeekerProfile(JobSeekerProfile profile);
    List<Application> findByJob(Job job);
    List<Application> findByJobCompanyId(Long companyId);
    Optional<Application> findByJobIdAndJobSeekerProfileId(Long jobId, Long profileId);
    long countByJobSeekerProfileAndStatus(JobSeekerProfile profile, ApplicationStatus status);
    long countByJobCompanyIdAndStatus(Long companyId, ApplicationStatus status);
    List<Application> findTop5ByJobSeekerProfileOrderByAppliedAtDesc(JobSeekerProfile profile);
    List<Application> findTop5ByJobCompanyIdOrderByAppliedAtDesc(Long companyId);
}