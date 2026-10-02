package com.job.jobapplication.repository;

import com.job.jobapplication.model.Application;
import com.job.jobapplication.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByApplication(Application application);
    List<Interview> findByApplicationJobCompanyIdOrderByInterviewDateDesc(Long companyId);
    @Modifying
    @Query("DELETE FROM Interview i WHERE i.application.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);
}