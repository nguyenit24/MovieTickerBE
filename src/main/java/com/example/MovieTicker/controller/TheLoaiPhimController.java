package com.example.MovieTicker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieTicker.entity.TheLoaiPhim;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.TheLoaiPhimService;

import jakarta.annotation.PostConstruct;


@RestController
@RequestMapping("/api/theloai")
public class TheLoaiPhimController {

    @Autowired
    private TheLoaiPhimService theLoaiPhimService;

    @PostConstruct
    public void init() {
        String[] defaultGenres = {"Hành động", "Tình cảm", "Kinh dị", "Hài", "Hoạt hình","Khoa học viễn tưởng","Phiêu lưu","Tâm lý","Chiến tranh","Thể thao","Âm nhạc","Tài liệu"};
        for (String ten : defaultGenres) {
            if (!theLoaiPhimService.existsByTenTheLoai(ten)) {
                TheLoaiPhim tlp = TheLoaiPhim.builder().tenTheLoai(ten).build();
                theLoaiPhimService.createTheLoaiPhim(tlp);
            }
        }
    }

    @GetMapping
    public ApiResponse<?> getAllTheLoaiPhim() {
        return ApiResponse.<Object>builder()
                .code(200)
                .message("Lấy danh sách thể loại phim thành công")
                .data(theLoaiPhimService.getAllTheLoaiPhim())
                .build();
    }


    @GetMapping("/{id}")
    public ApiResponse<?> getTheLoaiPhimById(@PathVariable String id) {
        var theLoaiPhim = theLoaiPhimService.getTheLoaiPhimById(id);
        if (theLoaiPhim != null) {
            return ApiResponse.<Object>builder()
                    .code(200)
                    .message("Lấy thể loại phim thành công")
                    .data(theLoaiPhim)
                    .build();
        } else {
            return ApiResponse.<Object>builder()
                    .code(404)
                    .message("Không tìm thấy thể loại phim với id: " + id)
                    .build();
        }
    }

    @PostMapping
    public ApiResponse<?> createTheLoaiPhim(@RequestBody TheLoaiPhim theLoaiPhim) {
        if (theLoaiPhimService.existsByTenTheLoai(theLoaiPhim.getTenTheLoai())) {
            return ApiResponse.<Object>builder()
                    .code(400)
                    .message("Thể loại phim với tên '" + theLoaiPhim.getTenTheLoai() + "' đã tồn tại")
                    .build();
        }
        var createdTheLoaiPhim = theLoaiPhimService.createTheLoaiPhim(theLoaiPhim);
        return ApiResponse.<Object>builder()
                .code(201)
                .message("Tạo thể loại phim thành công")
                .data(createdTheLoaiPhim)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateTheLoaiPhim(@PathVariable String id,@RequestBody TheLoaiPhim theLoaiPhim) {
        var updatedTheLoaiPhim = theLoaiPhimService.updateTheLoaiPhim(id, theLoaiPhim);
        if (updatedTheLoaiPhim != null) {
            return ApiResponse.<Object>builder()
                    .code(200)
                    .message("Cập nhật thể loại phim thành công")
                    .data(updatedTheLoaiPhim)
                    .build();
        } else {
            return ApiResponse.<Object>builder()
                    .code(404)
                    .message("Không tìm thấy thể loại phim với id: " + id)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteTheLoaiPhim(@PathVariable String id) {
        boolean deleted = theLoaiPhimService.deleteTheLoaiPhim(id);
        if (deleted) {
            return ApiResponse.<Object>builder()
                    .code(200)
                    .message("Xóa thể loại phim thành công")
                    .build();
        } else {
            return ApiResponse.<Object>builder()
                    .code(404)
                    .message("Không tìm thấy thể loại phim với id: " + id)
                    .build();
        }
    }

}
