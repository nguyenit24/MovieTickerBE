package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PhongChieu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String MaPhongChieu;

    @Column(length = 255, unique = true, nullable = false)
    private String TenPhong;

    private int SoLuongGhe;

    @OneToMany(mappedBy = "phongChieu", cascade = CascadeType.ALL)
    private List<Ghe> listGhe = new ArrayList<>();

    @OneToMany(mappedBy = "phongChieu", cascade = CascadeType.ALL)
    private List<SuatChieu> listSuatChieu = new ArrayList<>();
}
