/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *
 * @author ADAMS
 */
@Controller
public class HomeController {
    @GetMapping("/")
    public String Home(Model model){
       model.addAttribute("Title", "JobHub");
        return "home";
    }  
}
