package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SuatChieu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String MaSuatChieu;

    private int DonGiaCoSo;

    @Column(nullable = false)
    private LocalDate ThoiGianBatDau;

    @ManyToOne
    @JoinColumn(name = "MaPhim")
    private Phim phim;

    @ManyToOne
    @JoinColumn(name = "MaPhongChieu")
    private PhongChieu phongChieu;
}
