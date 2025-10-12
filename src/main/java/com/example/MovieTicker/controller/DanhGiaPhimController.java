package com.example.MovieTicker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieTicker.request.DanhGiaPhimRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.DanhGiaPhimService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/danhgiaphim")
public class DanhGiaPhimController {

    @Autowired
    private DanhGiaPhimService danhGiaPhimService;

    @GetMapping("/phim/{maPhim}")
    public ApiResponse<?> getDanhSachDanhGiaByPhimId(@PathVariable String maPhim) {
        return ApiResponse.<Object>builder()
                .code(200)
                .message("Lấy danh sách đánh giá phim theo mã phim thành công")
                .data(danhGiaPhimService.getDanhGiaByPhimId(maPhim))
                .build();
    }

    @PostMapping
    public ApiResponse<?> createDanhGiaPhim(@RequestBody DanhGiaPhimRequest request) {
        return ApiResponse.<Object>builder()
                .code(201)
                .message("Tạo đánh giá phim thành công")
                .data(danhGiaPhimService.saveDanhGiaPhim(request))
                .build();
    }
    

    @GetMapping
    public ApiResponse<?> getAllDanhGiaPhim() {
        return ApiResponse.<Object>builder()
                .code(200)
                .message("Lấy tất cả đánh giá phim thành công")
                .data(danhGiaPhimService.getAllDanhGiaPhim())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getDanhGiaPhimById(@PathVariable String id) {
        return ApiResponse.<Object>builder()
                .code(200)
                .message("Lấy đánh giá phim theo ID thành công")
                .data(danhGiaPhimService.getDanhGiaPhimById(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteDanhGiaPhim(@PathVariable String id) {
        danhGiaPhimService.deleteDanhGiaPhim(id);
        return ApiResponse.<Object>builder()
                .code(200)
                .message("Xóa đánh giá phim thành công")
                .build();
    }

}
