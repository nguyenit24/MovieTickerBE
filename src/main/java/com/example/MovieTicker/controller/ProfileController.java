// Tạo file mới trong: src/main/java/com/example/MovieTicker/controller/ProfileController.java
package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.User;
import com.example.MovieTicker.request.ChangePasswordRequest;
import com.example.MovieTicker.request.ProfileUpdateRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.TaiKhoanService;
import com.example.MovieTicker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final TaiKhoanService taiKhoanService;

    @GetMapping("/me")
    public ApiResponse<User> getMyInfo() {
        return ApiResponse.<User>builder()
                .data(userService.getMyInfo())
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<User> updateMyInfo(@RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<User>builder()
                .data(userService.updateMyInfo(request))
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        taiKhoanService.changePassword(request);
        return ApiResponse.<String>builder()
                .message("Change password successfully.")
                .build();
    }
}
