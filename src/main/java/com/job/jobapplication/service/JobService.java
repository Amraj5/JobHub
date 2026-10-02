/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;
import com.job.jobapplication.dto.JobForm;
import com.job.jobapplication.dto.JobSearchCriteria;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.exception.UnauthorizedActionException;
import com.job.jobapplication.model.*;
import com.job.jobapplication.repository.CompanyRepository;
import com.job.jobapplication.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.job.jobapplication.repository.ApplicationRepository;
import com.job.jobapplication.repository.InterviewRepository;

import java.util.List;
/**
 *
 * @author ADAMS
 */
@Service
@Transactional(readOnly = true)
public class JobService {
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public JobService(JobRepository jobRepository,
                      CompanyRepository companyRepository,
                      ApplicationRepository applicationRepository,
                      InterviewRepository interviewRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }
    
    public Page<Job> searchActiveJobs(JobSearchCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String keyword = isBlank(criteria.getKeyword()) ? null : criteria.getKeyword().trim();
        String location = isBlank(criteria.getLocation()) ? null : criteria.getLocation().trim();

        return jobRepository.searchJobs(
                JobStatus.ACTIVE,
                keyword,
                location,
                criteria.getCategory(),
                criteria.getEmploymentType(),
                criteria.getExperienceLevel(),
                pageable
        );
    }
    public List<Job> findAll() {
        return jobRepository.findAllWithCompany();
    }

    @Transactional
    public void forceDeleteJob(Long jobId) {
        Job job = findById(jobId);
        interviewRepository.deleteByJobId(jobId);
        applicationRepository.deleteByJobId(jobId);
        jobRepository.delete(job);
    }

    public long countByStatus(JobStatus status) {
        return jobRepository.countByStatus(status);
    }

    public Job findById(Long id) {
        return jobRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
    }

    public List<Job> findByCompany(Company company) {
        return jobRepository.findByCompany(company);
    }

    public List<Job> findActiveByCompany(Company company) {
        return jobRepository.findByCompany(company).stream()
                .filter(j -> j.getStatus() == JobStatus.ACTIVE)
                .toList();
    }

    public long countActiveByCompany(Company company) {
        return jobRepository.countByCompanyAndStatus(company, JobStatus.ACTIVE);
    }

    @Transactional
    public Job createJob(JobForm form, User employer) {
        Company company = companyRepository.findByOwner(employer)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You must create a company profile before posting jobs."));

        Job job = new Job();
        applyFormToJob(form, job);
        job.setCompany(company);
        job.setStatus(JobStatus.ACTIVE);

        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long jobId, JobForm form, User employer) {
        Job job = findById(jobId);
        assertOwnership(job, employer);
        applyFormToJob(form, job);
        return job;
    }

    @Transactional
    public void deleteJob(Long jobId, User employer) {
        Job job = findById(jobId);
        assertOwnership(job, employer);
        jobRepository.delete(job);
    }
    

    private void applyFormToJob(JobForm form, Job job) {
        job.setTitle(form.getTitle().trim());
        job.setDescription(form.getDescription());
        job.setRequirements(form.getRequirements());
        job.setCategory(form.getCategory());
        job.setLocation(form.getLocation().trim());
        job.setEmploymentType(form.getEmploymentType());
        job.setExperienceLevel(form.getExperienceLevel());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        job.setCurrency(form.getCurrency());
        job.setDeadline(form.getDeadline());
    }

    private void assertOwnership(Job job, User employer) {
        Company owner = job.getCompany();
        if (owner == null || owner.getOwner() == null
                || !owner.getOwner().getId().equals(employer.getId())) {
            throw new UnauthorizedActionException("You do not own this job posting.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
