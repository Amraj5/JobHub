package com.job.jobapplication.repository;

import com.job.jobapplication.model.JobSeekerProfile;
import com.job.jobapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {
    Optional<JobSeekerProfile> findByUser(User user);
    Optional<JobSeekerProfile> findByUserId(Long userId);
}