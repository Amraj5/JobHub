/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.service;

import com.job.jobapplication.dto.CompanyForm;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.exception.UnauthorizedActionException;
import com.job.jobapplication.model.Company;
import com.job.jobapplication.model.User;
import com.job.jobapplication.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author ADAMS
 */
@Service
@Transactional(readOnly = true)
public class CompanyService {
        private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }

    public Company findByOwner(User owner) {
        return companyRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You have not created a company profile yet."));
    }

    public Company findByOwnerOrNull(User owner) {
        return companyRepository.findByOwner(owner).orElse(null);
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    /**
     * Employer creates their company profile. One per user.
     */
    @Transactional
    public Company createCompany(CompanyForm form, User owner) {
        if (companyRepository.findByOwner(owner).isPresent()) {
            throw new BusinessRuleException("You already have a company profile.");
        }
        if (companyRepository.existsByName(form.getName().trim())) {
            throw new BusinessRuleException("A company with that name already exists.");
        }

        Company company = new Company();
        company.setOwner(owner);
        applyFormToCompany(form, company);
        return companyRepository.save(company);
    }

    /**
     * Employer updates their own company profile.
     */
    @Transactional
    public Company updateCompany(User owner, CompanyForm form) {
        Company company = findByOwner(owner);

        // If name is being changed, ensure new name is unique
        String newName = form.getName().trim();
        if (!company.getName().equalsIgnoreCase(newName)
                && companyRepository.existsByName(newName)) {
            throw new BusinessRuleException("A company with that name already exists.");
        }

        applyFormToCompany(form, company);
        return company;  // dirty checking saves
    }

    private void applyFormToCompany(CompanyForm form, Company company) {
        company.setName(form.getName().trim());
        company.setDescription(form.getDescription());
        company.setWebsite(form.getWebsite());
        company.setLocation(form.getLocation());
        company.setIndustry(form.getIndustry());
        company.setLogoUrl(form.getLogoUrl());
    }
}
