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
    private Long maDV;

    @Column(nullable = false)
    private String tenDV;

    @Column(nullable = false)
    private Double donGia;

    private String moTa;
}
