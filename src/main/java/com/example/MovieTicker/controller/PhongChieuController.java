package com.example.MovieTicker.controller;

import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.PhongChieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phong-chieu")
public class PhongChieuController {
    @Autowired
    private PhongChieuRepository phongChieuRepository;

    @GetMapping
    public List<PhongChieu> getAll() {
        return phongChieuRepository.findAll();
    }

    @GetMapping("/{id}")
    public PhongChieu getById(@PathVariable String id) {
        return phongChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
    }

    @PostMapping
    public PhongChieu create(@RequestBody PhongChieu phongChieu) {
        return phongChieuRepository.save(phongChieu);
    }

    @PutMapping("/{id}")
    public PhongChieu update(@PathVariable String id, @RequestBody PhongChieu phongChieu) {
        PhongChieu pc = phongChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        pc.setTenPhong(phongChieu.getTenPhong());
        pc.setSoLuongGhe(phongChieu.getSoLuongGhe());
        return phongChieuRepository.save(pc);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        phongChieuRepository.deleteById(id);
    }
}
