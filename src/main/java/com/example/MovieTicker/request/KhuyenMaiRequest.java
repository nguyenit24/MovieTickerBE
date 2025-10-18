package com.example.MovieTicker.request;

import java.time.LocalDate;
import java.util.List;

import com.example.MovieTicker.entity.VeKhuyenMai;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
public class KhuyenMaiRequest {
    private String tenKm;
    private String moTa;
    private Double giaTri;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String maCode;
    private Integer soLuong;
    private boolean trangThai;
    private String urlHinh;
}
