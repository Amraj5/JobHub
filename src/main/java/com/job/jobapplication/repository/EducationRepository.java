package com.job.jobapplication.repository;

import com.job.jobapplication.model.Education;
import com.job.jobapplication.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByJobSeekerProfile(JobSeekerProfile profile);
    void deleteByJobSeekerProfile(JobSeekerProfile profile);
}