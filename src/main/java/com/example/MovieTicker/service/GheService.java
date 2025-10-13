package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.Ghe;
import com.example.MovieTicker.entity.LoaiGhe;
import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.GheRepository;
import com.example.MovieTicker.repository.LoaiGheRepository;
import com.example.MovieTicker.repository.PhongChieuRepository;
import com.example.MovieTicker.request.GheRequest;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GheService {
    @Autowired
    private GheRepository gheRepository;

    @Autowired
    private LoaiGheRepository loaiGheRepository;
    
    @Autowired
    private PhongChieuRepository phongChieuRepository;

//    @PostConstruct
//    public void seedGhe() {
//        int soHang = 10;
//        int soCot = 10;
//
//        List<LoaiGhe> loaiGheList = loaiGheRepository.findAll();
//        for (int i = 0; i < soHang; i++) {
//            char hang = (char)('A' + i);
//            for (int j = 1; j <= soCot; j++) {
//                String tenGhe = String.format("%c%02d", hang, j);
//                LoaiGhe loaiGhe;
//                Optional<PhongChieu> phongChieu = phongChieuRepository.findById("P2");
//                if (phongChieu.isEmpty()) {
//                    throw new RuntimeException("Không tìm thấy phòng chiếu P3 (phòng VIP)");
//                }
//                if (i < 4) {
//                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("Thường");
//                } else if (i < 8) {
//                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("VIP");
//                } else {
//                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("Couple");
//                }
//                if (loaiGhe == null) {
//                    throw new RuntimeException("Loại ghế không tồn tại");
//                }
//                if (!gheRepository.existsByTenGheAndPhongChieu(tenGhe, phongChieu.get())) {
//                    Ghe ghe = Ghe.builder()
//                            .tenGhe(tenGhe)
//                            .loaiGhe(loaiGhe)
//                            .phongChieu(phongChieu.get())
//                            .build();
//                    gheRepository.save(ghe);
//                }
//            }
//        }
//    }

//    @PostConstruct
//    public void seedGhePhongVip() {
//        Optional<PhongChieu> phongChieuOpt = phongChieuRepository.findById("P3");
//        if (phongChieuOpt.isEmpty()) {
//            throw new RuntimeException("Không tìm thấy phòng chiếu P3 (phòng VIP)");
//        }
//
//        PhongChieu phongChieu = phongChieuOpt.get();
//
//        LoaiGhe loaiVip = loaiGheRepository.findByTenLoaiGhe("VIP");
//        LoaiGhe loaiCouple = loaiGheRepository.findByTenLoaiGhe("Couple");
//
//        if (loaiVip == null || loaiCouple == null) {
//            throw new RuntimeException("Chưa có loại ghế VIP hoặc Couple trong DB!");
//        }
//
//        // 🪑 Hàng A–C: ghế VIP, 8 cột mỗi hàng
//        for (int i = 0; i < 3; i++) {
//            char hang = (char) ('A' + i);
//            for (int j = 1; j <= 8; j++) {
//                String tenGhe = String.format("%c%02d", hang, j);
//                if (!gheRepository.existsByTenGheAndPhongChieu(tenGhe, phongChieu)) {
//                    Ghe ghe = Ghe.builder()
//                            .tenGhe(tenGhe)
//                            .loaiGhe(loaiVip)
//                            .phongChieu(phongChieu)
//                            .build();
//                    gheRepository.save(ghe);
//                }
//            }
//        }
//
//        // 💑 Hàng D–E: ghế Couple, 4 cột mỗi hàng
//        for (int i = 3; i < 5; i++) {
//            char hang = (char) ('A' + i);
//            for (int j = 1; j <= 4; j++) {
//                String tenGhe = String.format("%c%02d", hang, j);
//                if (!gheRepository.existsByTenGheAndPhongChieu(tenGhe, phongChieu)) {
//                    Ghe ghe = Ghe.builder()
//                            .tenGhe(tenGhe)
//                            .loaiGhe(loaiCouple)
//                            .phongChieu(phongChieu)
//                            .build();
//                    gheRepository.save(ghe);
//                }
//            }
//        }
//
//        System.out.println("✅ Đã seed ghế cho phòng VIP " + phongChieu.getTenPhong());
//    }


    public Ghe updateGhe(String id, GheRequest request) {
        Optional<Ghe> gheOptional = gheRepository.findById(id);
        
        if (gheOptional.isEmpty()) {
            return null;
        }
        
        Ghe ghe = gheOptional.get();
        
        if (request.getTenGhe() != null) {
            ghe.setTenGhe(request.getTenGhe());
        }
        
        if (request.getMaPhongChieu() != null) {
            Optional<PhongChieu> phongChieuOptional = phongChieuRepository.findById(request.getMaPhongChieu());
            phongChieuOptional.ifPresent(ghe::setPhongChieu);
        }
        
        if (request.getMaLoaiGhe() != null) {
            Optional<LoaiGhe> loaiGheOptional = loaiGheRepository.findById(request.getMaLoaiGhe());
            loaiGheOptional.ifPresent(ghe::setLoaiGhe);
        }
        
        return gheRepository.save(ghe);
    }

    public List<Ghe> findAllByPhongChieu(PhongChieu phongChieu) {
        return gheRepository.findAllByPhongChieu(phongChieu);
    }
}
