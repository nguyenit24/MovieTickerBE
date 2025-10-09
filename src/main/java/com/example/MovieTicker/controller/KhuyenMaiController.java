package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.KhuyenMai;
import com.example.MovieTicker.request.KhuyenMaiRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/khuyenmai")
public class KhuyenMaiController {
    
    @Autowired
    private KhuyenMaiService khuyenMaiService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<KhuyenMai>>> getAllKhuyenMai() {
        try {
            List<KhuyenMai> khuyenMaiList = khuyenMaiService.getAllKhuyenMai();
            return ResponseEntity.ok(
                ApiResponse.<List<KhuyenMai>>builder()
                    .code(200)
                    .message("Lấy danh sách khuyến mãi thành công")
                    .data(khuyenMaiList)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<KhuyenMai>>builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/valid")
    public ResponseEntity<ApiResponse<List<KhuyenMai>>> getValidPromotions() {
        try {
            List<KhuyenMai> khuyenMaiList = khuyenMaiService.getValidPromotions();
            return ResponseEntity.ok(
                ApiResponse.<List<KhuyenMai>>builder()
                    .code(200)
                    .message("Lấy danh sách khuyến mãi còn hiệu lực thành công")
                    .data(khuyenMaiList)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<KhuyenMai>>builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> getKhuyenMaiById(@PathVariable String id) {
        try {
            Optional<KhuyenMai> khuyenMai = khuyenMaiService.getKhuyenMaiById(id);
            if (khuyenMai.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponse.<KhuyenMai>builder()
                        .code(200)
                        .message("Lấy thông tin khuyến mãi thành công")
                        .data(khuyenMai.get())
                        .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<KhuyenMai>builder()
                        .code(404)
                        .message("Không tìm thấy khuyến mãi với mã: " + id)
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<KhuyenMai>builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KhuyenMai>>> searchKhuyenMai(@RequestParam String keyword) {
        try {
            List<KhuyenMai> khuyenMaiList = khuyenMaiService.searchKhuyenMai(keyword);
            return ResponseEntity.ok(
                ApiResponse.<List<KhuyenMai>>builder()
                    .code(200)
                    .message("Tìm kiếm khuyến mãi thành công")
                    .data(khuyenMaiList)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<KhuyenMai>>builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<KhuyenMai>> createKhuyenMai(@RequestBody KhuyenMaiRequest request) {
        try {
            KhuyenMai newKhuyenMai = khuyenMaiService.createKhuyenMai(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<KhuyenMai>builder()
                    .code(201)
                    .message("Tạo khuyến mãi thành công")
                    .data(newKhuyenMai)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<KhuyenMai>builder()
                    .code(400)
                    .message("Lỗi tạo khuyến mãi: " + e.getMessage())
                    .build());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> updateKhuyenMai(@PathVariable String id, @RequestBody KhuyenMaiRequest request) {
        try {
            KhuyenMai updatedKhuyenMai = khuyenMaiService.updateKhuyenMai(id, request);
            if (updatedKhuyenMai != null) {
                return ResponseEntity.ok(
                    ApiResponse.<KhuyenMai>builder()
                        .code(200)
                        .message("Cập nhật khuyến mãi thành công")
                        .data(updatedKhuyenMai)
                        .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<KhuyenMai>builder()
                        .code(404)
                        .message("Không tìm thấy khuyến mãi với mã: " + id)
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<KhuyenMai>builder()
                    .code(400)
                    .message("Lỗi cập nhật khuyến mãi: " + e.getMessage())
                    .build());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKhuyenMai(@PathVariable String id) {
        try {
            boolean deleted = khuyenMaiService.deleteKhuyenMai(id);
            if (deleted) {
                return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                        .code(200)
                        .message("Xóa khuyến mãi thành công")
                        .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                        .code(404)
                        .message("Không tìm thấy khuyến mãi với mã: " + id)
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/pageable")
    public ResponseEntity<ApiResponse<?>> getKhuyenMaiPageable(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 5;
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.getKhuyenMaiPage(pageable);
            Map<String, Object> response = Map.of(
                "totalPages", khuyenMaiPage.getTotalPages(),
                "currentItems", khuyenMaiPage.getContent(),
                "currentPage", khuyenMaiPage.getNumber() + 1
            );
            return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                    .code(200)
                    .message("Lấy danh sách khuyến mãi phân trang thành công")
                    .data(response)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                    .code(500)
                    .message("Lỗi server: " + e.getMessage())
                    .build());
        }
    }
    
}