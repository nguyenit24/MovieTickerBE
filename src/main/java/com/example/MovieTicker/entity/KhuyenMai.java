package com.example.MovieTicker.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "KhuyenMai")
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKm;

    @Column(nullable = false)
    private String tenKm;

    @Column(nullable = false)
    private String moTa;
    
    @Column(nullable = false)
    private Double giaTri;

    @Column(nullable = false)
    private LocalDate ngayBatDau;

    @Column(nullable = false)
    private LocalDate ngayKetThuc;

    @OneToMany(mappedBy = "khuyenMai")
    private List<VeKhuyenMai> ves;
}
