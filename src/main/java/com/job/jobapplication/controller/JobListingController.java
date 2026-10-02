/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.controller;
import com.job.jobapplication.dto.JobSearchCriteria;
import com.job.jobapplication.model.EmploymentType;
import com.job.jobapplication.model.ExperienceLevel;
import com.job.jobapplication.model.Industry;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.job.jobapplication.model.JobSeekerProfile;
import com.job.jobapplication.repository.ApplicationRepository;
import com.job.jobapplication.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import com.job.jobapplication.service.JobSeekerProfileService;
/**
 *
 * @author ADAMS
 */
@Controller
public class JobListingController {
    private final JobService jobService;
    private final JobSeekerProfileService profileService;
    private final ApplicationRepository applicationRepository;
    public JobListingController(JobService jobService, JobSeekerProfileService profileService,
                                ApplicationRepository applicationRepository) {
        this.jobService = jobService;
        this.profileService= profileService;
        this.applicationRepository= applicationRepository;
    }

    @GetMapping("/jobs")
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) EmploymentType type,
            @RequestParam(required = false) ExperienceLevel level,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        JobSearchCriteria criteria = new JobSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setLocation(location);
        criteria.setCategory(category);
        criteria.setEmploymentType(type);
        criteria.setExperienceLevel(level);

        Page<Job> jobsPage = jobService.searchActiveJobs(criteria, page, 10);

        model.addAttribute("jobsPage", jobsPage);
        model.addAttribute("criteria", criteria);
        model.addAttribute("categories", Industry.values());
        model.addAttribute("types", EmploymentType.values());
        model.addAttribute("levels", ExperienceLevel.values());

        return "jobs/list";
    }
    @GetMapping("/jobs/{id}")
    public String jobDetails(@PathVariable Long id,
                             Model model,
                             Authentication authentication) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);

        boolean alreadyApplied = false;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_JOB_SEEKER"))) {
            try {
                JobSeekerProfile profile = profileService.findByUser(
                    ((UserPrincipal) authentication.getPrincipal()).getUser());
                alreadyApplied = applicationRepository
                    .findByJobIdAndJobSeekerProfileId(id, profile.getId())
                    .isPresent();
            } catch (Exception ignored) {}
        }
        model.addAttribute("alreadyApplied", alreadyApplied);

        return "jobs/details";
    }
}
