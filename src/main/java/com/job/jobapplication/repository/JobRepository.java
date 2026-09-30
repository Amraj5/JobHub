package com.job.jobapplication.repository;

import com.job.jobapplication.model.Company;
import com.job.jobapplication.model.Industry;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompany(Company company);
    List<Job> findByStatus(JobStatus status);
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
    Page<Job> findByStatusAndCategory(JobStatus status, Industry category, Pageable pageable);
    Page<Job> findByStatusAndLocationContainingIgnoreCase(JobStatus status, String location, Pageable pageable);
    long countByCompanyAndStatus(Company company, JobStatus status);
}