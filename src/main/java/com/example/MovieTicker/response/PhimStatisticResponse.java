package com.example.MovieTicker.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhimStatisticResponse {
    private String tenPhim;
    private String soLuongSuatChieu;
    private String tongDoanhThu;
    private String thoiGian;
}
