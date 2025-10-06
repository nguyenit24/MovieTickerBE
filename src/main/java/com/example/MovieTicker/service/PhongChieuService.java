package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.PhongChieuRepository;
import jakarta.annotation.PostConstruct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PhongChieuService {
    @Autowired
    private PhongChieuRepository phongChieuRepository;

    @PostConstruct
    public void seedPhongChieu() {
        String[] defaultPhong = {"Phòng 1", "Phòng 2", "Phòng VIP"};
        for (String ten : defaultPhong) {
            if (!phongChieuRepository.existsByTenPhong(ten)) {
                PhongChieu pc = PhongChieu.builder().tenPhong(ten).build();
                phongChieuRepository.save(pc);
            }
        }
    }

    public List<PhongChieu> getListPhongChieu() {
        return phongChieuRepository.findAll();
    }
    
    public Page<PhongChieu> getPhongChieuPage(Pageable pageable) {
        return phongChieuRepository.findAll(pageable);
    }
}
