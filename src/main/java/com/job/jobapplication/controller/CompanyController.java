package com.job.jobapplication.controller;

import com.job.jobapplication.model.Company;
import com.job.jobapplication.service.CompanyService;
import com.job.jobapplication.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CompanyController {

    private final CompanyService companyService;
    private final JobService jobService;

    public CompanyController(CompanyService companyService, JobService jobService) {
        this.companyService = companyService;
        this.jobService = jobService;
    }

    @GetMapping("/companies")
    public String listCompanies(Model model) {
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "companies/list";
    }

    @GetMapping("/companies/{id}")
    public String companyDetail(@PathVariable Long id, Model model) {
        Company company = companyService.findById(id);
        model.addAttribute("company", company);
        model.addAttribute("jobs", jobService.findActiveByCompany(company));
        return "companies/detail";
    }
}