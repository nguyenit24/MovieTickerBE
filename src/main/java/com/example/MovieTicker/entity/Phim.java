package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.engine.internal.Cascade;

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
    @Column(unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String TenPhim;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String MoTa;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String DaoDien;
    @Column(columnDefinition = "NVARCHAR(500)")
    private String DienVien;
    private int ThoiLuong;
    private LocalDate NgayKhoiChieu;
    @Column(columnDefinition = "VARCHAR(500)")
    private String HinhAnh;
    @Column(columnDefinition = "VARCHAR(500)")
    private String TrailerURL;
    private int Tuoi;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String TrangThai;

    @ManyToMany(mappedBy = "listPhim",  fetch = FetchType.LAZY)
    private List<TheLoaiPhim> listTheLoai = new ArrayList<>();

    @OneToMany(mappedBy = "Phim", cascade = CascadeType.ALL)
    private List<SuatChieu> listSuatChieu = new ArrayList<>();
}
