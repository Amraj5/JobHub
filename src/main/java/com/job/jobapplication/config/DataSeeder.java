package com.job.jobapplication.config;

import com.job.jobapplication.model.*;
import com.job.jobapplication.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final JobRepository jobRepo;
    private final JobSeekerProfileRepository profileRepo;
    private final ApplicationRepository applicationRepo;
    private final EducationRepository educationRepo;
    private final SkillRepository skillRepo;
    private final InterviewRepository interviewRepo;
    private final EmployerProfileRepository employerProfileRepo;
    
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, CompanyRepository companyRepo,
                      JobRepository jobRepo, JobSeekerProfileRepository profileRepo,
                      ApplicationRepository applicationRepo, EducationRepository educationRepo,
                      SkillRepository skillRepo, InterviewRepository interviewRepo,
                      EmployerProfileRepository employerProfileRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.jobRepo = jobRepo;
        this.profileRepo = profileRepo;
        this.applicationRepo = applicationRepo;
        this.educationRepo = educationRepo;
        this.skillRepo = skillRepo;
        this.interviewRepo = interviewRepo;
        this.encoder= encoder;
        this.employerProfileRepo = employerProfileRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepo.count() > 0) {
            System.out.println(">>> DataSeeder: database already has users, skipping.");
            return;
        }

        System.out.println(">>> DataSeeder: starting...");

        Map<String, User> usersByEmail = seedUsers();
        seedEmployerProfiles(usersByEmail);
        seedJobSeekerProfiles(usersByEmail);
        Map<String, Company> companiesByName = seedCompanies(usersByEmail);
        Map<String, Skill> skillsByName = seedSkills();
        Map<String, Job> jobsByKey = seedJobs(companiesByName);
        linkProfileSkills(usersByEmail, skillsByName);
        seedEducation(usersByEmail);
        Map<String, Application> appsByKey = seedApplications(usersByEmail, jobsByKey);
        seedInterviews(appsByKey);

        System.out.println(">>> DataSeeder: done.");
    }

    // ============ USERS ============
    private Map<String, User> seedUsers() throws Exception {
        Map<String, User> map = new HashMap<>();
        for (String[] row : readCsv("seed/users.csv")) {
            User u = new User();
            u.setEmail(row[0]);
            u.setUsername(row[1]);
            u.setPassword(encoder.encode(row[2]));
            u.setFullName(row[3]);
            u.setPhone(row[4]);
            u.setRole(Role.valueOf(row[5]));
            u.setActive(Boolean.parseBoolean(row[6]));
            userRepo.save(u);
            map.put(u.getEmail(), u);
        }
        System.out.println(">>> Users: " + map.size());
        return map;
    }

    // ============ EMPLOYER PROFILES ============
    private void seedEmployerProfiles(Map<String, User> usersByEmail) throws Exception {
        int count = 0;
        for (String[] row : readCsv("seed/employer_profiles.csv")) {
            User u = usersByEmail.get(row[0]);
            if (u == null) continue;
            EmployerProfile ep = new EmployerProfile();
            ep.setUser(u);
            ep.setJobTitle(row[1]);
            ep.setDepartment(row[2]);
            ep.setWorkEmail(row[3]);
            ep.setLinkedinUrl(row[4]);
            employerProfileRepo.save(ep);
            count++;
        }
        System.out.println(">>> EmployerProfiles: " + count);
    }

    // ============ JOB SEEKER PROFILES ============
    private void seedJobSeekerProfiles(Map<String, User> usersByEmail) throws Exception {
        int count = 0;
        for (String[] row : readCsv("seed/profiles.csv")) {
            User u = usersByEmail.get(row[0]);
            if (u == null) continue;
            JobSeekerProfile p = new JobSeekerProfile();
            p.setUser(u);
            p.setTitle(row[1]);
            p.setBio(row[2]);
            p.setLocation(row[3]);
            p.setYearsOfExperience(Integer.parseInt(row[4]));
            p.setCvUrl(row[5]);
            p.setProfilePictureUrl(row[6]);
            profileRepo.save(p);
            count++;
        }
        System.out.println(">>> JobSeekerProfiles: " + count);
    }

    // ============ COMPANIES ============
    private Map<String, Company> seedCompanies(Map<String, User> usersByEmail) throws Exception {
        Map<String, Company> map = new HashMap<>();
        for (String[] row : readCsv("seed/companies.csv")) {
            Company c = new Company();
            c.setName(row[0]);
            c.setDescription(row[1]);
            c.setWebsite(row[2]);
            c.setLocation(row[3]);
            c.setIndustry(Industry.valueOf(row[4]));
            c.setLogoUrl(row[5]);
            User owner = usersByEmail.get(row[6]);
            c.setOwner(owner);
            companyRepo.save(c);
            map.put(c.getName(), c);
        }
        System.out.println(">>> Companies: " + map.size());
        return map;
    }

    // ============ SKILLS ============
    private Map<String, Skill> seedSkills() throws Exception {
        Map<String, Skill> map = new HashMap<>();
        for (String[] row : readCsv("seed/skills.csv")) {
            Skill s = new Skill();
            s.setName(row[0]);
            skillRepo.save(s);
            map.put(s.getName(), s);
        }
        System.out.println(">>> Skills: " + map.size());
        return map;
    }

    // ============ JOBS ============
    private Map<String, Job> seedJobs(Map<String, Company> companiesByName) throws Exception {
        Map<String, Job> map = new HashMap<>();
        for (String[] row : readCsv("seed/jobs.csv")) {
            Job j = new Job();
            j.setTitle(row[0]);
            j.setDescription(row[1]);
            j.setRequirements(row[2]);
            j.setCategory(Industry.valueOf(row[3]));
            j.setLocation(row[4]);
            j.setEmploymentType(EmploymentType.valueOf(row[5]));
            j.setExperienceLevel(ExperienceLevel.valueOf(row[6]));
            j.setSalaryMin(Long.parseLong(row[7]));
            j.setSalaryMax(Long.parseLong(row[8]));
            j.setCurrency(row[9]);
            j.setDeadline(LocalDate.parse(row[10]));
            j.setStatus(JobStatus.valueOf(row[11]));
            j.setCompany(companiesByName.get(row[12]));
            jobRepo.save(j);
            map.put(j.getTitle() + "|" + j.getCompany().getName(), j);
        }
        System.out.println(">>> Jobs: " + map.size());
        return map;
    }

    // ============ LINK PROFILE ↔ SKILLS ============
    private void linkProfileSkills(Map<String, User> usersByEmail, Map<String, Skill> skillsByName) {
        // Assign a subset of skills to each seeker
        Map<String, List<String>> profileSkills = Map.of(
            "john.doe@gmail.com", List.of("Java", "Spring Boot", "PostgreSQL", "REST APIs", "Git"),
            "fatima.ibrahim@outlook.com", List.of("SQL", "Excel", "Data Analysis", "Financial Modeling"),
            "emeka.nwosu@yahoo.com", List.of("Docker", "Kubernetes", "AWS", "Git", "Node.js"),
            "aisha.bello@gmail.com", List.of("Patient Care", "Clinical Research"),
            "david.chen@gmail.com", List.of("Python", "Machine Learning", "TensorFlow", "Data Analysis"),
            "priya.sharma@outlook.com", List.of("SEO", "Content Marketing", "Data Analysis"),
            "seun.adebayo@gmail.com", List.of("AutoCAD", "Excel", "Financial Modeling")
        );

        int count = 0;
        for (Map.Entry<String, List<String>> entry : profileSkills.entrySet()) {
            User u = usersByEmail.get(entry.getKey());
            if (u == null) continue;
            JobSeekerProfile p = profileRepo.findByUser(u).orElse(null);
            if (p == null) continue;
            for (String skillName : entry.getValue()) {
                Skill s = skillsByName.get(skillName);
                if (s != null) {
                    p.getSkills().add(s);
                    count++;
                }
            }
            profileRepo.save(p);
        }
        System.out.println(">>> Profile-Skill links: " + count);
    }

    // ============ EDUCATION ============
    private void seedEducation(Map<String, User> usersByEmail) throws Exception {
        int count = 0;
        for (String[] row : readCsv("seed/education.csv")) {
            User u = usersByEmail.get(row[0]);
            if (u == null) continue;
            JobSeekerProfile p = profileRepo.findByUser(u).orElse(null);
            if (p == null) continue;
            Education e = new Education();
            e.setJobSeekerProfile(p);
            e.setQualification(row[1]);
            e.setInstitution(row[2]);
            e.setStartYear(Integer.parseInt(row[3]));
            e.setEndYear(Integer.parseInt(row[4]));
            educationRepo.save(e);
            count++;
        }
        System.out.println(">>> Education: " + count);
    }

    // ============ APPLICATIONS ============
    private Map<String, Application> seedApplications(Map<String, User> usersByEmail,
                                                       Map<String, Job> jobsByKey) throws Exception {
        Map<String, Application> map = new HashMap<>();
        for (String[] row : readCsv("seed/applications.csv")) {
            User u = usersByEmail.get(row[0]);
            if (u == null) continue;
            JobSeekerProfile p = profileRepo.findByUser(u).orElse(null);
            if (p == null) continue;
            Job j = jobsByKey.get(row[1] + "|" + row[2]);
            if (j == null) continue;

            Application a = new Application();
            a.setJobSeekerProfile(p);
            a.setJob(j);
            a.setCvUrl(row[3]);
            a.setCoverLetter(row[4]);
            a.setStatus(ApplicationStatus.valueOf(row[5]));
            applicationRepo.save(a);
            map.put(u.getEmail() + "|" + j.getTitle() + "|" + j.getCompany().getName(), a);
        }
        System.out.println(">>> Applications: " + map.size());
        return map;
    }

    // ============ INTERVIEWS ============
    private void seedInterviews(Map<String, Application> appsByKey) throws Exception {
        int count = 0;
        for (String[] row : readCsv("seed/interviews.csv")) {
            String key = row[0] + "|" + row[1] + "|" + row[2];
            Application a = appsByKey.get(key);
            if (a == null) continue;
            Interview i = new Interview();
            i.setApplication(a);
            i.setInterviewDate(LocalDate.parse(row[3]));
            i.setInterviewTime(LocalTime.parse(row[4]));
            i.setInterviewType(row[5]);
            i.setMeetingLink(row[6]);
            interviewRepo.save(i);
            count++;
        }
        System.out.println(">>> Interviews: " + count);
    }

    // ============ CSV READER ============
    private List<String[]> readCsv(String resourcePath) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(resourcePath).getInputStream()))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }   // skip header
                if (line.isBlank()) continue;
                rows.add(line.split(","));
            }
        }
        return rows;
    }
}