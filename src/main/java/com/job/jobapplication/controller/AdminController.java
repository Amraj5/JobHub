package com.job.jobapplication.controller;

import com.job.jobapplication.model.*;
import com.job.jobapplication.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public AdminController(UserService userService,
                           CompanyService companyService,
                           JobService jobService,
                           ApplicationService applicationService) {
        this.userService = userService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> allUsers = userService.findAll();
        List<Company> allCompanies = companyService.findAll();
        List<Job> allJobs = jobService.findAll();

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("seekers",
                allUsers.stream().filter(u -> u.getRole() == Role.JOB_SEEKER).count());
        model.addAttribute("employers",
                allUsers.stream().filter(u -> u.getRole() == Role.EMPLOYER).count());
        model.addAttribute("totalCompanies", allCompanies.size());
        model.addAttribute("totalJobs", allJobs.size());
        model.addAttribute("activeJobs",
                allJobs.stream().filter(j -> j.getStatus() == JobStatus.ACTIVE).count());
        model.addAttribute("totalApplications", applicationService.countAll());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.toggleActive(id);
        redirectAttrs.addFlashAttribute("successMessage", "User status updated.");
        return "redirect:/admin/users";
    }

    @GetMapping("/companies")
    public String companies(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "admin/companies";
    }

    @GetMapping("/jobs")
    public String jobs(Model model) {
        model.addAttribute("jobs", jobService.findAll());
        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        jobService.forceDeleteJob(id);
        redirectAttrs.addFlashAttribute("successMessage", "Job removed.");
        return "redirect:/admin/jobs";
    }

    @GetMapping("/applications")
    public String applications(Model model) {
        model.addAttribute("applications", applicationService.findAll());
        return "admin/applications";
    }
}