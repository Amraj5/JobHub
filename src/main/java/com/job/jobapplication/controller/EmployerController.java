package com.job.jobapplication.controller;

import com.job.jobapplication.dto.CompanyForm;
import com.job.jobapplication.dto.JobForm;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.model.*;
import com.job.jobapplication.security.UserPrincipal;
import com.job.jobapplication.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final EmployerProfileService employerProfileService;

    public EmployerController(ApplicationService applicationService,
                              CompanyService companyService,
                              JobService jobService,
                              EmployerProfileService employerProfileService) {
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.employerProfileService = employerProfileService;
    }

    // ============ DASHBOARD ============
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User employer = principal.getUser();
        Company company = companyService.findByOwnerOrNull(employer);

        model.addAttribute("user", employer);

        if (company == null) {
            model.addAttribute("noCompany", true);
            return "employer/dashboard";
        }

        model.addAttribute("company", company);
        model.addAttribute("activeJobs", jobService.countActiveByCompany(company));
        model.addAttribute("totalApplications",
                applicationService.findByCompanyId(company.getId()).size());
        model.addAttribute("shortlistedCount",
                applicationService.countByCompanyAndStatus(company.getId(), ApplicationStatus.SHORTLISTED));
        model.addAttribute("interviewCount",
                applicationService.countByCompanyAndStatus(company.getId(), ApplicationStatus.INTERVIEW));
        model.addAttribute("recentApplications",
                applicationService.findRecentByCompanyId(company.getId()));

        return "employer/dashboard";
    }

    // ============ COMPANY PROFILE ============
    @GetMapping("/company")
    public String companyForm(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User employer = principal.getUser();
        Company company = companyService.findByOwnerOrNull(employer);

        CompanyForm form = new CompanyForm();
        if (company != null) {
            form.setName(company.getName());
            form.setDescription(company.getDescription());
            form.setWebsite(company.getWebsite());
            form.setLocation(company.getLocation());
            form.setIndustry(company.getIndustry());
            form.setLogoUrl(company.getLogoUrl());
        }

        model.addAttribute("companyForm", form);
        model.addAttribute("company", company);
        return "employer/company-form";
    }

    @PostMapping("/company")
    public String saveCompany(@Valid @ModelAttribute("companyForm") CompanyForm form,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserPrincipal principal,
                              Model model,
                              RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", companyService.findByOwnerOrNull(principal.getUser()));
            return "employer/company-form";
        }

        try {
            User employer = principal.getUser();
            if (companyService.findByOwnerOrNull(employer) == null) {
                companyService.createCompany(form, employer);
            } else {
                companyService.updateCompany(employer, form);
            }
            redirectAttrs.addFlashAttribute("successMessage", "Company profile saved.");
            return "redirect:/employer/dashboard";
        } catch (BusinessRuleException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("company", companyService.findByOwnerOrNull(principal.getUser()));
            return "employer/company-form";
        }
    }

    // ============ MANAGE JOBS ============
    @GetMapping("/jobs")
    public String myJobs(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User employer = principal.getUser();
        Company company = companyService.findByOwnerOrNull(employer);
        model.addAttribute("company", company);
        if (company != null) {
            model.addAttribute("jobs", jobService.findByCompany(company));
        }
        return "employer/jobs";
    }

    // ============ POST JOB ============
    @GetMapping("/jobs/new")
    public String newJobForm(Model model) {
        model.addAttribute("jobForm", new JobForm());
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserPrincipal principal,
                            Model model,
                            RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "employer/job-form";
        }
        try {
            jobService.createJob(form, principal.getUser());
            redirectAttrs.addFlashAttribute("successMessage", "Job posted successfully.");
            return "redirect:/employer/jobs";
        } catch (ResourceNotFoundException | BusinessRuleException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "employer/job-form";
        }
    }

    // ============ EDIT JOB ============
    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id,
                              @AuthenticationPrincipal UserPrincipal principal,
                              Model model) {
        Job job = jobService.findById(id);

        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setDescription(job.getDescription());
        form.setRequirements(job.getRequirements());
        form.setCategory(job.getCategory());
        form.setLocation(job.getLocation());
        form.setEmploymentType(job.getEmploymentType());
        form.setExperienceLevel(job.getExperienceLevel());
        form.setSalaryMin(job.getSalaryMin());
        form.setSalaryMax(job.getSalaryMax());
        form.setCurrency(job.getCurrency());
        form.setDeadline(job.getDeadline());

        model.addAttribute("jobForm", form);
        model.addAttribute("jobId", id);
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserPrincipal principal,
                            Model model,
                            RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/job-form";
        }
        try {
            jobService.updateJob(id, form, principal.getUser());
            redirectAttrs.addFlashAttribute("successMessage", "Job updated.");
            return "redirect:/employer/jobs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("jobId", id);
            return "employer/job-form";
        }
    }

    // ============ DELETE JOB ============
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserPrincipal principal,
                            RedirectAttributes redirectAttrs) {
        jobService.deleteJob(id, principal.getUser());
        redirectAttrs.addFlashAttribute("successMessage", "Job deleted.");
        return "redirect:/employer/jobs";
    }

    // ============ APPLICANTS ============
    @GetMapping("/jobs/{id}/applicants")
    public String applicants(@PathVariable Long id, Model model) {
        Job job = jobService.findById(id);
        List<Application> apps = applicationService.findByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", apps);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "employer/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ApplicationStatus status,
                               @AuthenticationPrincipal UserPrincipal principal,
                               RedirectAttributes redirectAttrs) {
        Application app = applicationService.updateStatus(id, status, principal.getUser());
        redirectAttrs.addFlashAttribute("successMessage", "Application status updated.");
        return "redirect:/employer/jobs/" + app.getJob().getId() + "/applicants";
    }
}