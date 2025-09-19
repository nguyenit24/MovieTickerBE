package com.example.MovieTicker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieTicker.request.UserCreationRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.response.UserResponse;
import com.example.MovieTicker.service.UserService;

import jakarta.validation.Valid;

@RestController
@Valid
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .data(userService.createUser(request))
                .build();
        return response;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable("id") String id) {
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .data(userService.getUserById(id))
                .build();
        return response;
    }

    @PostMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String id, @RequestBody UserCreationRequest request) {
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(id, request))
                .build();
        return response;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAllUsers())
                .build();
        return response;
    }

    @GetMapping("/google")
    public ApiResponse<Map<String, Object>> usergoogle(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> userInfo = Map.of(
            "name", principal.getAttribute("name"),
            "email", principal.getAttribute("email"),
            "picture", principal.getAttribute("picture")
        );
        return ApiResponse.<Map<String, Object>>builder()
                .data(userInfo)
                .build();
    }
}
