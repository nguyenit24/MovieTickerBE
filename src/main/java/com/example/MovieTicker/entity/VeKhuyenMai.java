package com.example.MovieTicker.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ve_KhuyenMai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeKhuyenMai {

    @EmbeddedId
    private VeKhuyenMaiId id;

    @ManyToOne
    @MapsId("maVe")
    @JoinColumn(name = "MaVe")
    private Ve ve;

    @ManyToOne
    @MapsId("maKm")
    @JoinColumn(name = "maKm")
    private KhuyenMai khuyenMai;

    @Column(name = "GiaTriGiam", nullable = false)
    private Double giaTriGiam;

    @Column(name = "PhanTramGiam", nullable = false)
    private Double phanTramGiam;

    @Column(name = "NgayApDung", nullable = false)
    private LocalDate ngayApDung;
}
