package com.example.MovieTicker.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Ve")
public class Ve {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maVe;

    @ManyToOne
    @JoinColumn(name = "maHD", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "maUser", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "maSuatChieu", nullable = false)
    private SuatChieu suatChieu; 
   
    @ManyToOne
    @JoinColumn(name = "maGhe", nullable = false)
    private Ghe ghe; 

    @Column(nullable = false)
    private LocalDateTime ngayDat;

    @Column(nullable = false)
    private Double thanhTien;

    // 1 Vé có nhiều ChiTietDichVuVe
    @OneToMany(mappedBy = "ve", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDichVuVe> chiTietDichVuVes;

    @OneToMany(mappedBy = "ve")
    private List<VeKhuyenMai> khuyenMais;
}
