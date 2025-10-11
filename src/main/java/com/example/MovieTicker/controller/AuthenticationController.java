package com.example.MovieTicker.controller;

import com.example.MovieTicker.request.AuthenticateRequest;
import com.example.MovieTicker.request.IntrospectRequest;
import com.example.MovieTicker.request.RegistrationRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.response.AuthenticateResponse;
import com.example.MovieTicker.response.IntrospectResponse;
import com.example.MovieTicker.service.AuthenticateService;
import com.example.MovieTicker.service.TaiKhoanService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticateService authenticateService;
    private final TaiKhoanService taiKhoanService;

    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> login(@RequestBody AuthenticateRequest request) {
        var result = authenticateService.authenticated(request);
        return ApiResponse.<AuthenticateResponse>builder()
                .message("Authentication successful")
                .data(result)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegistrationRequest request) {
        taiKhoanService.register(request);
        return ApiResponse.<Void>builder()
                .code(201)
                .message("User registered successfully")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        authenticateService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticateService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().data(result).build();
    }
}
