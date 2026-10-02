package com.job.jobapplication.repository;

import com.job.jobapplication.model.Company;
import com.job.jobapplication.model.Industry;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.job.jobapplication.model.EmploymentType;
import com.job.jobapplication.model.ExperienceLevel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompany(Company company);
    List<Job> findByStatus(JobStatus status);
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
    Page<Job> findByStatusAndCategory(JobStatus status, Industry category, Pageable pageable);
    Page<Job> findByStatusAndLocationContainingIgnoreCase(JobStatus status, String location, Pageable pageable);
    long countByCompanyAndStatus(Company company, JobStatus status);
    long countByStatus(JobStatus status);
    @Query("SELECT j FROM Job j JOIN FETCH j.company")
    List<Job> findAllWithCompany();
    @Modifying
    @Query("DELETE FROM Application a WHERE a.job.id = :jobId")
    void deleteApplicationsByJobId(@Param("jobId") Long jobId);
    
   @Query("SELECT j FROM Job j JOIN FETCH j.company " +
       "WHERE j.status = :status " +
       "AND (CAST(:keyword AS string) IS NULL OR " +
       "     LOWER(j.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
       "     LOWER(j.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
       "AND (CAST(:location AS string) IS NULL OR " +
       "     LOWER(j.location) LIKE LOWER(CONCAT('%', CAST(:location AS string), '%'))) " +
       "AND (:category IS NULL OR j.category = :category) " +
       "AND (:type IS NULL OR j.employmentType = :type) " +
       "AND (:level IS NULL OR j.experienceLevel = :level)")
    Page<Job> searchJobs(@Param("status") JobStatus status,
                         @Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("category") Industry category,
                         @Param("type") EmploymentType type,
                         @Param("level") ExperienceLevel level,
                         Pageable pageable);
    @Query("SELECT j FROM Job j JOIN FETCH j.company WHERE j.id = :id")
    Optional<Job> findByIdWithCompany(@Param("id") Long id);
}