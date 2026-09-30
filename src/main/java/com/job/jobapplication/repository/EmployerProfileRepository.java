package com.job.jobapplication.repository;

import com.job.jobapplication.model.EmployerProfile;
import com.job.jobapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    Optional<EmployerProfile> findByUser(User user);
    Optional<EmployerProfile> findByUserId(Long userId);
}