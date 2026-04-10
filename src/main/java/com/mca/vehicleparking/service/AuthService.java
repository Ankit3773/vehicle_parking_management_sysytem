package com.mca.vehicleparking.service;

import com.mca.vehicleparking.config.AdminSessionInterceptor;
import com.mca.vehicleparking.dto.AdminProfileResponse;
import com.mca.vehicleparking.dto.LoginRequest;
import com.mca.vehicleparking.dto.LoginResponse;
import com.mca.vehicleparking.exception.UnauthorizedException;
import com.mca.vehicleparking.model.Admin;
import com.mca.vehicleparking.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Validates admin credentials and stores a simple session for protected APIs.
    public LoginResponse login(LoginRequest request, HttpSession session) {
        Admin admin = adminRepository.findByUsernameIgnoreCaseAndActiveTrue(request.username().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password."));

        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        session.setAttribute(AdminSessionInterceptor.ADMIN_ID, admin.getId());
        session.setAttribute("ADMIN_NAME", admin.getFullName());

        return new LoginResponse(
                "Login successful.",
                new AdminProfileResponse(admin.getId(), admin.getUsername(), admin.getFullName())
        );
    }

    // Reads the current logged-in admin from the HTTP session.
    public AdminProfileResponse getLoggedInAdmin(HttpSession session) {
        Object adminId = session.getAttribute(AdminSessionInterceptor.ADMIN_ID);
        if (adminId == null) {
            throw new UnauthorizedException("Please log in to continue.");
        }

        Admin admin = adminRepository.findById((Long) adminId)
                .orElseThrow(() -> new UnauthorizedException("Session is invalid. Please log in again."));

        return new AdminProfileResponse(admin.getId(), admin.getUsername(), admin.getFullName());
    }

    // Clears the session during logout.
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
