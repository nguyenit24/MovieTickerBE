package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.LoaiGhe;
import com.example.MovieTicker.entity.Ghe;
import com.example.MovieTicker.repository.LoaiGheRepository;
import com.example.MovieTicker.repository.GheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loai-ghe")
public class LoaiGheController {
    @Autowired
    private LoaiGheRepository loaiGheRepository;
    @Autowired
    private GheRepository gheRepository;

    @GetMapping
    public List<LoaiGhe> getAllLoaiGhe() {
        return loaiGheRepository.findAll();
    }

    @GetMapping("/{id}")
    public LoaiGhe getLoaiGheById(@PathVariable String id) {
        return loaiGheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại ghế"));
    }

    @PostMapping
    public LoaiGhe createLoaiGhe(@RequestBody LoaiGhe loaiGhe) {
        return loaiGheRepository.save(loaiGhe);
    }

    @PutMapping("/{id}")
    public LoaiGhe updateLoaiGhe(@PathVariable String id, @RequestBody LoaiGhe loaiGhe) {
        LoaiGhe lg = loaiGheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại ghế"));
        lg.setTenLoaiGhe(loaiGhe.getTenLoaiGhe());
        lg.setPhuThu(loaiGhe.getPhuThu());
        return loaiGheRepository.save(lg);
    }

    @DeleteMapping("/{id}")
    public void deleteLoaiGhe(@PathVariable String id) {
        loaiGheRepository.deleteById(id);
    }

    
    @GetMapping("/{id}/ghe")
    public List<Ghe> getGheByLoaiGhe(@PathVariable String id) {
        LoaiGhe lg = loaiGheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại ghế"));
        return gheRepository.findAll().stream()
                .filter(ghe -> ghe.getLoaiGhe() != null && ghe.getLoaiGhe().getMaLoaiGhe().equals(id))
                .toList();
    }
}
