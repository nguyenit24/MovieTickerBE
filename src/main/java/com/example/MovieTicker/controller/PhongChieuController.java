package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.PhongChieuRepository;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.PhongChieuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/phongchieu")
public class PhongChieuController {
   @Autowired
   PhongChieuService phongChieuService;

   @GetMapping
   public ApiResponse<?> getListPhongChieu() {
         return ApiResponse.<List<PhongChieu>>builder()
           .code(200)
           .message("Lấy danh sách phòng chiếu thành công")
           .data(phongChieuService.getListPhongChieu())
           .build();
   }
   
   @GetMapping("/pageable")
   public ApiResponse<Map<String, Object>> getPhongChieuPageable(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
       if (page < 1) page = 1;
       if (size < 1) size = 5;
       Pageable pageable = PageRequest.of(page - 1, size);
       Page<PhongChieu> phongChieuPage = phongChieuService.getPhongChieuPage(pageable);
       Map<String, Object> response = Map.of(
           "totalPages", phongChieuPage.getTotalPages(),
           "currentItems", phongChieuPage.getContent(),
           "currentPage", phongChieuPage.getNumber() + 1
       );
       return ApiResponse.<Map<String, Object>>builder()
           .code(HttpStatus.OK.value())
           .message("Lấy danh sách phòng chiếu phân trang thành công")
           .data(response)
           .build();
   }
}
