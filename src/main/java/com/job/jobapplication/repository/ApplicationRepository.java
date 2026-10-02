package com.job.jobapplication.repository;

import com.job.jobapplication.model.Application;
import com.job.jobapplication.model.ApplicationStatus;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.JobSeekerProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByJobIdAndJobSeekerProfileId(Long jobId, Long profileId);

    long countByJobSeekerProfileAndStatus(JobSeekerProfile profile, ApplicationStatus status);

    long countByJobCompanyIdAndStatus(Long companyId, ApplicationStatus status);

    // ---------- SEEKER SIDE ----------
    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH j.company " +
           "WHERE a.jobSeekerProfile = :profile " +
           "ORDER BY a.appliedAt DESC")
    List<Application> findBySeekerWithDetails(@Param("profile") JobSeekerProfile profile);

    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH j.company " +
           "WHERE a.jobSeekerProfile = :profile " +
           "ORDER BY a.appliedAt DESC")
    List<Application> findRecentBySeekerWithDetails(@Param("profile") JobSeekerProfile profile,
                                                     Pageable pageable);

    // ---------- EMPLOYER SIDE ----------
    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH j.company " +
           "JOIN FETCH a.jobSeekerProfile p " +
           "JOIN FETCH p.user " +
           "WHERE j.company.id = :companyId " +
           "ORDER BY a.appliedAt DESC")
    List<Application> findByCompanyWithDetails(@Param("companyId") Long companyId);

    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH j.company " +
           "JOIN FETCH a.jobSeekerProfile p " +
           "JOIN FETCH p.user " +
           "WHERE j.company.id = :companyId " +
           "ORDER BY a.appliedAt DESC")
    List<Application> findRecentByCompanyWithDetails(@Param("companyId") Long companyId,
                                                     Pageable pageable);

    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.jobSeekerProfile p " +
           "JOIN FETCH p.user " +
           "WHERE a.job = :job " +
           "ORDER BY a.appliedAt DESC")
    List<Application> findByJobWithDetails(@Param("job") Job job);
    
    @Query("SELECT a FROM Application a " +
       "JOIN FETCH a.job j " +
       "JOIN FETCH j.company " +
       "JOIN FETCH a.jobSeekerProfile p " +
       "JOIN FETCH p.user")
    List<Application> findAllWithDetails();
    @Modifying
    @Query("DELETE FROM Application a WHERE a.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);
}