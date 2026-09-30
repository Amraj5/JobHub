/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.dto;
import com.job.jobapplication.model.EmploymentType;
import com.job.jobapplication.model.ExperienceLevel;
import com.job.jobapplication.model.Industry;
/**
 *
 * @author ADAMS
 */
public class JobSearchCriteria {
    private String keyword;
    private String location;
    private Industry category;
    private EmploymentType employmentType;
    private ExperienceLevel experienceLevel;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Industry getCategory() {
        return category;
    }

    public void setCategory(Industry category) {
        this.category = category;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
