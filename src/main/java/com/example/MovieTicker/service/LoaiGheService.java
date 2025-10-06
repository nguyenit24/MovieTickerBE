package com.example.MovieTicker.service;

import java.util.List;

import com.example.MovieTicker.entity.LoaiGhe;
import com.example.MovieTicker.repository.LoaiGheRepository;
import com.example.MovieTicker.repository.GheRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoaiGheService {
    @Autowired
    private LoaiGheRepository loaiGheRepository;
    @Autowired
    private GheRepository gheRepository;

    @PostConstruct
    public void seedLoaiGhe() {
        String[] defaultTypes = {"Thường", "VIP", "Couple"};
        for (String ten : defaultTypes) {
            if (!loaiGheRepository.existsByTenLoaiGhe(ten)) {
                LoaiGhe lg = LoaiGhe.builder().tenLoaiGhe(ten)
                .build();
                if(ten.equals("VIP")){
                    float phuthu = 20000f;
                    lg.setPhuThu(phuthu);
                } else if(ten.equals("Couple")){
                    float phuthu = 40000f;
                    lg.setPhuThu(phuthu);
                } else{
                    lg.setPhuThu(0f);
                }
                loaiGheRepository.save(lg);
            }
        }
    }

    public List<LoaiGhe> getAllLoaiGhe() {
        return loaiGheRepository.findAll();
    }
}
