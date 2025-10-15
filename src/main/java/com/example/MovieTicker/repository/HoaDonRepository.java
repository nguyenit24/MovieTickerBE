package com.example.MovieTicker.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.MovieTicker.entity.Phim;
import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.response.HoaDonResponse;
import com.example.MovieTicker.response.HoaDonSatisticResponse;
import com.example.MovieTicker.response.PhimStatisticResponse;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MovieTicker.entity.HoaDon;
import com.example.MovieTicker.entity.User;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    
    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = :trangThai AND h.ngayLap < :expiredTime")
    List<HoaDon> findExpiredInvoicesByStatus(@Param("trangThai") String trangThai, @Param("expiredTime") LocalDateTime expiredTime);
    
    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = 'PROCESSING' AND h.ngayLap < :expiredTime")
    List<HoaDon> findExpiredProcessingInvoices(@Param("expiredTime") LocalDateTime expiredTime);


    @Query("""
    SELECT new com.example.MovieTicker.response.HoaDonSatisticResponse(
        h.maHD,
        h.ngayLap,
        h.trangThai,
        h.tongTien,
        COUNT(v),
        s.thoiGianBatDau,
        p.tenPhim,
        pc.tenPhong
    )
    FROM HoaDon h
    JOIN h.ves v
    JOIN v.suatChieu s
    JOIN s.phim p
    JOIN s.phongChieu pc
    WHERE h.trangThai = 'PAID'
    GROUP BY h.maHD, h.ngayLap, h.trangThai, h.tongTien, s.thoiGianBatDau, p.tenPhim, pc.tenPhong
""")
    List<HoaDonSatisticResponse> findAllHoaDonPaid();


    @Query("""
    SELECT new com.example.MovieTicker.response.PhimStatisticResponse(
        p.tenPhim,
        s.thoiGianBatDau,
        COUNT(v)
    )
    FROM HoaDon h
    JOIN h.ves v
    JOIN v.suatChieu s
    JOIN s.phim p
    WHERE h.trangThai = 'PAID'
   GROUP BY p.tenPhim, s.thoiGianBatDau
   ORDER BY p.tenPhim, s.thoiGianBatDau
    """)
    List<PhimStatisticResponse> findAllPhimStatistic();


    List<HoaDon> findByUserOrderByNgayLapDesc(User user);
}
