package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.PhongChieuRepository;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.PhongChieuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
   
}
