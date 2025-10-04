package com.example.MovieTicker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.MovieTicker.entity.Phim;
import com.example.MovieTicker.repository.PhimRepository;
import com.example.MovieTicker.request.PhimRequest;

@Service
public class PhimService {

    @Autowired
    private PhimRepository phimRepository;

    // Tạo phim mới
    public Phim createPhim(PhimRequest request) {
        // Kiểm tra phim đã tồn tại chưa
        if (phimRepository.existsByTenPhim(request.getTenPhim())) {
            throw new RuntimeException("Phim với tên '" + request.getTenPhim() + "' đã tồn tại");
        }

        Phim phim = Phim.builder()
                .tenPhim(request.getTenPhim())
                .moTa(request.getMoTa())
                .daoDien(request.getDaoDien())
                .dienVien(request.getDienVien())
                .thoiLuong(request.getThoiLuong())
                .ngayKhoiChieu(request.getNgayKhoiChieu())
                .hinhAnh(request.getHinhAnh())
                .trailerURL(request.getTrailerURL())
                .tuoi(request.getTuoi())
                .trangThai(request.getTrangThai())
                .build();

        return phimRepository.save(phim);
    }

    // Lấy tất cả phim
    public List<Phim> getAllPhim() {
        return phimRepository.findAll();
    }

    // Lấy phim theo ID
    public Phim getPhimById(String maPhim) {
        return phimRepository.findById(maPhim)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với mã: " + maPhim));
    }

    // Lấy phim theo tên
    public Phim getPhimByTen(String tenPhim) {
        return phimRepository.findByTenPhim(tenPhim)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với tên: " + tenPhim));
    }

    // Cập nhật phim
    public Phim updatePhim(String maPhim, PhimRequest request) {
        Phim existingPhim = getPhimById(maPhim);
        
        // Kiểm tra tên phim mới có bị trùng không (nếu tên khác với tên hiện tại)
        if (!existingPhim.getTenPhim().equals(request.getTenPhim()) && 
            phimRepository.existsByTenPhim(request.getTenPhim())) {
            throw new RuntimeException("Phim với tên '" + request.getTenPhim() + "' đã tồn tại");
        }

        existingPhim.setTenPhim(request.getTenPhim());
        existingPhim.setMoTa(request.getMoTa());
        existingPhim.setDaoDien(request.getDaoDien());
        existingPhim.setDienVien(request.getDienVien());
        existingPhim.setThoiLuong(request.getThoiLuong());
        existingPhim.setNgayKhoiChieu(request.getNgayKhoiChieu());
        existingPhim.setHinhAnh(request.getHinhAnh());
        existingPhim.setTrailerURL(request.getTrailerURL());
        existingPhim.setTuoi(request.getTuoi());
        existingPhim.setTrangThai(request.getTrangThai());

        return phimRepository.save(existingPhim);
    }

    // Xóa phim
    public void deletePhim(String maPhim) {
        Phim phim = getPhimById(maPhim);
        phimRepository.delete(phim);
    }

    // Tìm kiếm phim theo từ khóa trong tên
    public List<Phim> searchPhimByTen(String keyword) {
        return phimRepository.findByTenPhimContainingIgnoreCase(keyword);
    }

    // Lấy phim theo trạng thái
    public List<Phim> getPhimByTrangThai(String trangThai) {
        return phimRepository.findByTrangThai(trangThai);
    }

    // Lấy phim đang chiếu
    public List<Phim> getActivePhim() {
        return phimRepository.findActiveMovies();
    }

    // Lấy phim theo đạo diễn
    public List<Phim> getPhimByDaoDien(String daoDien) {
        return phimRepository.findByDaoDien(daoDien);
    }

    // Lấy phim theo độ tuổi
    public List<Phim> getPhimByTuoi(int tuoi) {
        return phimRepository.findByTuoi(tuoi);
    }

    // Lấy phim theo khoảng thời gian khởi chiếu
    public List<Phim> getPhimByNgayKhoiChieu(LocalDate startDate, LocalDate endDate) {
        return phimRepository.findByNgayKhoiChieuBetween(startDate, endDate);
    }

    // Lấy phim theo khoảng thời lượng
    public List<Phim> getPhimByThoiLuong(int minDuration, int maxDuration) {
        return phimRepository.findByThoiLuongBetween(minDuration, maxDuration);
    }

    // Lấy phim mới nhất
    public List<Phim> getLatestPhim() {
        return phimRepository.findAllByOrderByNgayKhoiChieuDesc();
    }

    // Kiểm tra phim có tồn tại không
    public boolean existsByTenPhim(String tenPhim) {
        return phimRepository.existsByTenPhim(tenPhim);
    }
}