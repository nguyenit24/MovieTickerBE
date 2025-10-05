package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.Ghe;
import com.example.MovieTicker.entity.LoaiGhe;
import com.example.MovieTicker.repository.GheRepository;
import com.example.MovieTicker.repository.LoaiGheRepository;

import jakarta.annotation.PostConstruct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GheService {
    @Autowired
    private GheRepository gheRepository;

    @Autowired
    private LoaiGheRepository loaiGheRepository;

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
}
