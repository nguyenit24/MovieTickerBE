package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.LoaiGhe;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.service.LoaiGheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class LoaiGheController {
   @Autowired
   LoaiGheService loaiGheService;

   @GetMapping("/loaighe")
   public ApiResponse<?> getAllLoaiGhe() {
       List<LoaiGhe> list = loaiGheService.getAllLoaiGhe();
       return ApiResponse.<List<LoaiGhe>>builder()
               .code(200)
               .message("Lấy danh sách loại ghế thành công")
               .data(list)
               .build();
   }
}
