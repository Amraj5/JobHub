/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.job.jobapplication.security;
import com.job.jobapplication.model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 *
 * @author ADAMS
 */
@Component
public class RoleBasedAuthSuccessHandler implements AuthenticationSuccessHandler{
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Role role = principal.getUser().getRole();

        String target = switch (role) {
            case ADMIN      -> "/admin/dashboard";
            case EMPLOYER   -> "/employer/dashboard";
            case JOB_SEEKER -> "/seeker/dashboard";
        };

        response.sendRedirect(target);
    }
}
