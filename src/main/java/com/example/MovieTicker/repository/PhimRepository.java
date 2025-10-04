package com.example.MovieTicker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieTicker.entity.Phim;

@Repository
public interface PhimRepository extends JpaRepository<Phim, String> {
    // Tìm phim theo tên
    Optional<Phim> findByTenPhim(String tenPhim);
    
    // Kiểm tra phim có tồn tại theo tên
    boolean existsByTenPhim(String tenPhim);
    
    // Tìm phim theo trạng thái
    List<Phim> findByTrangThai(String trangThai);
    
    // Tìm phim theo đạo diễn
    List<Phim> findByDaoDien(String daoDien);
    
    // Tìm phim theo độ tuổi
    List<Phim> findByTuoi(int tuoi);
    
    // Tìm phim phát hành trong khoảng thời gian
    List<Phim> findByNgayKhoiChieuBetween(LocalDate startDate, LocalDate endDate);
    
    // Tìm phim theo thời lượng
    List<Phim> findByThoiLuongBetween(int minDuration, int maxDuration);
    
    // Tìm phim có tên chứa từ khóa (không phân biệt hoa thường)
    @Query("SELECT p FROM Phim p WHERE LOWER(p.tenPhim) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Phim> findByTenPhimContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Tìm phim đang chiếu (trạng thái khác "Ngừng chiếu")
    @Query("SELECT p FROM Phim p WHERE p.trangThai != 'Ngừng chiếu'")
    List<Phim> findActiveMovies();
    
    // Tìm phim mới nhất (sắp xếp theo ngày khởi chiếu giảm dần)
    @Query("SELECT p FROM Phim p ORDER BY p.ngayKhoiChieu DESC")
    List<Phim> findAllByOrderByNgayKhoiChieuDesc();
}