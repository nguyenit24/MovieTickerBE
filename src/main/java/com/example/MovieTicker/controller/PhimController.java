package com.example.MovieTicker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.MovieTicker.entity.Phim;
import com.example.MovieTicker.request.PhimRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.PhimService;

@RestController
@RequestMapping("/phim")
public class PhimController {

    @Autowired
    private PhimService phimService;

    // Tạo phim mới
    @PostMapping
    public ApiResponse<Phim> createPhim(@RequestBody PhimRequest request) {
        try {
            Phim phim = phimService.createPhim(request);
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.CREATED.value())
                    .message("Tạo phim thành công")
                    .data(phim)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    // Lấy tất cả phim
    @GetMapping
    public ApiResponse<List<Phim>> getAllPhim() {
        List<Phim> phimList = phimService.getAllPhim();
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách phim thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo ID
    @GetMapping("/{maPhim}")
    public ApiResponse<Phim> getPhimById(@PathVariable String maPhim) {
        try {
            Phim phim = phimService.getPhimById(maPhim);
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy thông tin phim thành công")
                    .data(phim)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    // Cập nhật phim
    @PutMapping("/{maPhim}")
    public ApiResponse<Phim> updatePhim(@PathVariable String maPhim, @RequestBody PhimRequest request) {
        try {
            Phim phim = phimService.updatePhim(maPhim, request);
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật phim thành công")
                    .data(phim)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    // Xóa phim
    @DeleteMapping("/{maPhim}")
    public ApiResponse<String> deletePhim(@PathVariable String maPhim) {
        try {
            phimService.deletePhim(maPhim);
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa phim thành công")
                    .data("Phim với mã " + maPhim + " đã được xóa")
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    // Tìm kiếm phim theo tên
    @GetMapping("/search")
    public ApiResponse<List<Phim>> searchPhim(@RequestParam String keyword) {
        List<Phim> phimList = phimService.searchPhimByTen(keyword);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Tìm kiếm phim thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo trạng thái
    @GetMapping("/trang-thai/{trangThai}")
    public ApiResponse<List<Phim>> getPhimByTrangThai(@PathVariable String trangThai) {
        List<Phim> phimList = phimService.getPhimByTrangThai(trangThai);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim theo trạng thái thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim đang chiếu
    @GetMapping("/dang-chieu")
    public ApiResponse<List<Phim>> getActivePhim() {
        List<Phim> phimList = phimService.getActivePhim();
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim đang chiếu thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo đạo diễn
    @GetMapping("/dao-dien/{daoDien}")
    public ApiResponse<List<Phim>> getPhimByDaoDien(@PathVariable String daoDien) {
        List<Phim> phimList = phimService.getPhimByDaoDien(daoDien);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim theo đạo diễn thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo độ tuổi
    @GetMapping("/tuoi/{tuoi}")
    public ApiResponse<List<Phim>> getPhimByTuoi(@PathVariable int tuoi) {
        List<Phim> phimList = phimService.getPhimByTuoi(tuoi);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim theo độ tuổi thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo khoảng ngày khởi chiếu
    @GetMapping("/ngay-khoi-chieu")
    public ApiResponse<List<Phim>> getPhimByNgayKhoiChieu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Phim> phimList = phimService.getPhimByNgayKhoiChieu(startDate, endDate);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim theo ngày khởi chiếu thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo khoảng thời lượng
    @GetMapping("/thoi-luong")
    public ApiResponse<List<Phim>> getPhimByThoiLuong(
            @RequestParam int minDuration,
            @RequestParam int maxDuration) {
        List<Phim> phimList = phimService.getPhimByThoiLuong(minDuration, maxDuration);
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim theo thời lượng thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim mới nhất
    @GetMapping("/moi-nhat")
    public ApiResponse<List<Phim>> getLatestPhim() {
        List<Phim> phimList = phimService.getLatestPhim();
        return ApiResponse.<List<Phim>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy phim mới nhất thành công")
                .data(phimList)
                .build();
    }

    // Lấy phim theo tên
    @GetMapping("/ten/{tenPhim}")
    public ApiResponse<Phim> getPhimByTen(@PathVariable String tenPhim) {
        try {
            Phim phim = phimService.getPhimByTen(tenPhim);
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.OK.value())
                    .message("Lấy phim theo tên thành công")
                    .data(phim)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<Phim>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    // Kiểm tra phim có tồn tại theo tên
    @GetMapping("/exists/{tenPhim}")
    public ApiResponse<Boolean> checkPhimExists(@PathVariable String tenPhim) {
        boolean exists = phimService.existsByTenPhim(tenPhim);
        return ApiResponse.<Boolean>builder()
                .code(HttpStatus.OK.value())
                .message("Kiểm tra phim tồn tại thành công")
                .data(exists)
                .build();
    }
}