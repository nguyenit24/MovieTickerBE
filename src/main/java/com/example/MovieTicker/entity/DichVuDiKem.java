package com.example.MovieTicker.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DichVuDiKem")
public class DichVuDiKem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDv;

    @Column(nullable = false)
    private String tenDv;

    @Column(nullable = false)
    private Double donGia;

    private String moTa;
}
