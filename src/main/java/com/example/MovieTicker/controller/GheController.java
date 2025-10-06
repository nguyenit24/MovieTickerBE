package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.Ghe;
import com.example.MovieTicker.request.GheRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.GheService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ghe")
public class GheController {
    @Autowired
    private GheService gheService;

    @PutMapping("/{id}")
    public ApiResponse<?> updateGhe(@PathVariable String id, @RequestBody GheRequest request) {
        Ghe updatedGhe = gheService.updateGhe(id, request);
        if (updatedGhe != null) {
            return ApiResponse.<Ghe>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật ghế thành công")
                    .data(updatedGhe)
                    .build();
        } else {
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Không tìm thấy ghế với mã: " + id)
                    .build();
        }
    }  
}