# JobHub

A job recruitment platform built with Spring Boot and PostgreSQL.

## Status

In active development. Core backend and authentication are complete. Feature pages are being built.

## Stack

- Java 23, Spring Boot 3.4, Maven
- PostgreSQL 17, Spring Data JPA, Hibernate
- Spring Security 6, Thymeleaf, Bootstrap 5

## What Works

- Full domain model (10 entities with proper JPA relationships)
- Database schema auto-generated and validated
- Seed data loaded from CSV on startup
- Registration with validation and duplicate checks
- Login, logout, role-based redirects
- Role-protected routes (seeker / employer / admin)
- Business services with ownership checks and transactions

## What's Next

- Job listing, job details, apply flow
- Seeker dashboard, profile, applications
- Employer dashboard, post job, review applicants
- Admin dashboard and moderation
- Error pages, deployment

## Setup

Requirements: JDK 17+, Maven, PostgreSQL.

Create a database called `jobman`.

Edit `src/main/resources/application.properties`:

    spring.datasource.url=jdbc:postgresql://localhost:5432/jobman
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.open-in-view=false

Run:

    mvn spring-boot:run

Open http://localhost:8080

On first run, the schema is created and sample data is loaded automatically.

## Test Accounts

Password for all accounts: **password123**

| Role | Email |
|---|---|
| Admin | admin@jobhub.com |
| Seeker | john.doe@gmail.com |
| Seeker | fatima.ibrahim@outlook.com |
| Employer | ada.okafor@techcorp.ng |
| Employer | grace.eze@finbank.ng |

## Structure

    src/main/java/com/job/jobapplication/
      config/       BeanConfig, SecurityConfig, DataSeeder
      controller/   HTTP endpoints
      dto/          Request/response objects
      exception/    Custom exceptions
      model/        JPA entities and enums
      repository/   Spring Data repositories
      security/     UserPrincipal, CustomUserDetailsService
      service/      Business logic

    src/main/resources/
      seed/         CSV files for seeding
      static/       CSS, JS, images
      templates/    Thymeleaf pages and fragments

## Sample Data

15 users, 7 companies, 35 jobs, 29 applications, 30 skills, 12 education records, 3 interviews.

## Author

Adam — student project, 2026
