package com.mca.vehicleparking.controller;

import com.mca.vehicleparking.dto.AdminProfileResponse;
import com.mca.vehicleparking.dto.ApiMessageResponse;
import com.mca.vehicleparking.dto.LoginRequest;
import com.mca.vehicleparking.dto.LoginResponse;
import com.mca.vehicleparking.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return authService.login(request, session);
    }

    @GetMapping("/me")
    public AdminProfileResponse getCurrentAdmin(HttpSession session) {
        return authService.getLoggedInAdmin(session);
    }

    @PostMapping("/logout")
    public ApiMessageResponse logout(HttpSession session) {
        authService.logout(session);
        return new ApiMessageResponse("Logout successful.");
    }
}
