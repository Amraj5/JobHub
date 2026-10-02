package com.job.jobapplication.controller;

import com.job.jobapplication.dto.JobSearchCriteria;
import com.job.jobapplication.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final JobService jobService;

    public HomeController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "JobHub - Find Your Dream Job");

        // Fetch the 6 most recent active jobs for the featured section
        Page<?> featured = jobService.searchActiveJobs(new JobSearchCriteria(), 0, 6);
        model.addAttribute("featuredJobs", featured.getContent());
        model.addAttribute("totalJobs", featured.getTotalElements());

        return "home";
    }
}