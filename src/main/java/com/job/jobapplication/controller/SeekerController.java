package com.job.jobapplication.controller;

import com.job.jobapplication.exception.ResourceNotFoundException;
import com.job.jobapplication.model.*;
import com.job.jobapplication.security.UserPrincipal;
import com.job.jobapplication.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/seeker")
public class SeekerController {

    private final ApplicationService applicationService;
    private final JobSeekerProfileService profileService;
    private final JobService jobService;
    private final FileStorageService fileStorageService;

    public SeekerController(ApplicationService applicationService,
                            JobSeekerProfileService profileService,
                            JobService jobService,
                            FileStorageService fileStorageService) {
        this.applicationService = applicationService;
        this.profileService = profileService;
        this.jobService = jobService;
        this.fileStorageService = fileStorageService;
    }
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User seeker = principal.getUser();

        model.addAttribute("user", seeker);
        model.addAttribute("appliedCount",
                applicationService.countBySeeker(seeker, ApplicationStatus.PENDING)
                + applicationService.countBySeeker(seeker, ApplicationStatus.REVIEWING));
        model.addAttribute("shortlistedCount",
                applicationService.countBySeeker(seeker, ApplicationStatus.SHORTLISTED));
        model.addAttribute("interviewCount",
                applicationService.countBySeeker(seeker, ApplicationStatus.INTERVIEW));
        model.addAttribute("recentApplications", applicationService.findTop5BySeeker(seeker));

        // Recommended: latest 6 active jobs (simple heuristic for now)
        var page = jobService.searchActiveJobs(new com.job.jobapplication.dto.JobSearchCriteria(), 0, 6);
        model.addAttribute("recommended", page.getContent());

        return "seeker/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User seeker = principal.getUser();
        JobSeekerProfile profile = profileService.findByUser(seeker);
        model.addAttribute("user", seeker);
        model.addAttribute("profile", profile);
        return "seeker/profile";
    }

    @GetMapping("/applications")
    public String applications(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User seeker = principal.getUser();
        List<Application> applications = applicationService.findBySeeker(seeker);
        model.addAttribute("applications", applications);
        return "seeker/applications";
    }
    @PostMapping("/profile/cv")
    public String uploadCv(@RequestParam("cvFile") MultipartFile cvFile,
                           @AuthenticationPrincipal UserPrincipal principal,
                           RedirectAttributes redirectAttrs) {
        try {
            String cvPath = fileStorageService.saveCv(cvFile);
            profileService.updateProfile(principal.getUser(), null, null, null, null, cvPath, null);
            redirectAttrs.addFlashAttribute("successMessage", "CV uploaded successfully.");
        } catch (BusinessRuleException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/seeker/profile";
    }
}