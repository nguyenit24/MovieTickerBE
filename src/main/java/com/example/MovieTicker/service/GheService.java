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

    @PostConstruct
    public void seedGhe() {
        int soHang = 10;
        int soCot = 10;
    
        List<LoaiGhe> loaiGheList = loaiGheRepository.findAll();

        if (loaiGheList.isEmpty()) return;

        for (int i = 0; i < soHang; i++) {
            char hang = (char)('A' + i);
            for (int j = 1; j <= soCot; j++) {
                String tenGhe = String.format("%c%02d", hang, j);
                LoaiGhe loaiGhe;
                if (i < 4) {
                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("Thường");
                } else if (i < 8) {
                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("VIP");
                } else {
                    loaiGhe = loaiGheRepository.findByTenLoaiGhe("Couple");
                }
                if (loaiGhe == null) {
                    throw new RuntimeException("Loại ghế không tồn tại");
                }
                if (!gheRepository.existsByTenGhe(tenGhe)) {
                    Ghe ghe = Ghe.builder()
                            .tenGhe(tenGhe)
                            .loaiGhe(loaiGhe)
                            .build();
                    gheRepository.save(ghe);
                }
            }
        }
    }
    
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
    
}
