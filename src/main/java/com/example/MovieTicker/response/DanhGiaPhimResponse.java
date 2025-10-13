package com.example.MovieTicker.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DanhGiaPhimResponse {
    private Double rating;
    private String comment;
    private String tenNguoiDung;
    private LocalDate ngayDanhGia;
}
