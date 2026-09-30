/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.model;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author ADAMS
 */

@Entity
@Table(name = "skills")
public class Skill {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<JobSeekerProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<JobSeekerProfile> profiles) {
        this.profiles = profiles;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "skills")
    private Set<JobSeekerProfile> profiles = new HashSet<>();
}
