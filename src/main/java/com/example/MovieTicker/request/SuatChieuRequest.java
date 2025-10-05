package com.example.MovieTicker.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class SuatChieuRequest {
    private int donGiaCoSo;
    private LocalDate thoiGianBatDau;
    private String maPhim;
    private String maPhongChieu;
}
