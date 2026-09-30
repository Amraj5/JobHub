package com.job.jobapplication.repository;

import com.job.jobapplication.model.Application;
import com.job.jobapplication.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByApplication(Application application);
    List<Interview> findByApplicationJobCompanyIdOrderByInterviewDateDesc(Long companyId);
}