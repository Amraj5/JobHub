package com.job.jobapplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployerController {
    @GetMapping("/employer/dashboard")
    public String dashboard() { return "redirect:/jobs"; }
}