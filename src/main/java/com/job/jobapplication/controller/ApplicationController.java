package com.job.jobapplication.controller;

import com.job.jobapplication.dto.ApplicationForm;
import com.job.jobapplication.exception.BusinessRuleException;
import com.job.jobapplication.model.Job;
import com.job.jobapplication.model.User;
import com.job.jobapplication.security.UserPrincipal;
import com.job.jobapplication.service.ApplicationService;
import com.job.jobapplication.service.JobService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.job.jobapplication.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/seeker/apply")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final FileStorageService fileStorageService;

    public ApplicationController(ApplicationService applicationService,
                                 JobService jobService,
                                 FileStorageService fileStorageService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{jobId}")
    public String applyForm(@PathVariable Long jobId, Model model) {
        Job job = jobService.findById(jobId);
        model.addAttribute("job", job);
        model.addAttribute("applicationForm", new ApplicationForm());
        return "applications/apply";
    }

    @PostMapping("/{jobId}")
    public String apply(@PathVariable Long jobId,
                        @Valid @ModelAttribute("applicationForm") ApplicationForm form,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UserPrincipal principal,
                        Model model,
                        RedirectAttributes redirectAttrs) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("job", jobService.findById(jobId));
            return "applications/apply";
        }

        try {
            User seeker = principal.getUser();
            applicationService.apply(jobId, form, seeker);
            redirectAttrs.addFlashAttribute("successMessage",
                "Application submitted successfully!");
            return "redirect:/seeker/applications";
        } catch (BusinessRuleException e) {
            model.addAttribute("job", jobService.findById(jobId));
            model.addAttribute("errorMessage", e.getMessage());
            return "applications/apply";
        }
    }
}