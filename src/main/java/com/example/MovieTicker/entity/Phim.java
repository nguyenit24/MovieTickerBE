package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String MaPhim;
    @Column(unique = true, nullable = false, length = 255)
    private String TenPhim;
    @Column(length = 1000)
    private String MoTa;
    @Column(length = 255)
    private String DaoDien;
    @Column(length = 500)
    private String DienVien;
    private int ThoiLuong;
    private LocalDate NgayKhoiChieu;
    @Column(length = 500)
    private String HinhAnh;
    @Column(length = 500)
    private String TrailerURL;
    private int Tuoi;
    @Column(length = 255)
    private String TrangThai;

    @ManyToMany(mappedBy = "listPhim",  fetch = FetchType.LAZY)
    private List<TheLoaiPhim> listTheLoai = new ArrayList<>();

    @OneToMany(mappedBy = "phim", cascade = CascadeType.ALL)
    private List<SuatChieu> listSuatChieu = new ArrayList<>();
}
