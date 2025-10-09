package com.example.MovieTicker.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class KhuyenMaiRequest {
    private String tenKm;
    private String moTa;
    private Double giaTri;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}
