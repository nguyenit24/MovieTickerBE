package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ghe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maGhe;

    @Column(columnDefinition = "NVARCHAR(500)", nullable = false)
    private String tenGhe;

    @ManyToOne
    @JoinColumn(name = "MaPhongChieu")
    private PhongChieu phongChieu;

    @ManyToOne
    @JoinColumn(name = "MaLoaiGhe")
    private LoaiGhe loaiGhe;
}
