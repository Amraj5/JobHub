package com.job.jobapplication.repository;

import com.job.jobapplication.model.Company;
import com.job.jobapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByOwner(User owner);
    Optional<Company> findByName(String name);
    boolean existsByName(String name);
}