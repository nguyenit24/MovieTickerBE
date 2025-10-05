package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.SuatChieu;
import com.example.MovieTicker.entity.Phim;
import com.example.MovieTicker.entity.PhongChieu;
import com.example.MovieTicker.repository.SuatChieuRepository;
import com.example.MovieTicker.repository.PhimRepository;
import com.example.MovieTicker.repository.PhongChieuRepository;
import com.example.MovieTicker.request.SuatChieuRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuatChieuService {
    @Autowired
    private SuatChieuRepository suatChieuRepository;
    @Autowired
    private PhimRepository phimRepository;
    @Autowired
    private PhongChieuRepository phongChieuRepository;

    public SuatChieu createSuatChieu(SuatChieuRequest request) {
        Phim phim = phimRepository.findById(request.getMaPhim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        PhongChieu phongChieu = phongChieuRepository.findById(request.getMaPhongChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        SuatChieu suatChieu = SuatChieu.builder()
                .donGiaCoSo(request.getDonGiaCoSo())
                .thoiGianBatDau(request.getThoiGianBatDau())
                .phim(phim)
                .phongChieu(phongChieu)
                .build();
        return suatChieuRepository.save(suatChieu);
    }

    public List<SuatChieu> getAllSuatChieu() {
        return suatChieuRepository.findAll();
    }

    public SuatChieu getSuatChieuById(String id) {
        return suatChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));
    }

    public SuatChieu updateSuatChieu(String id, SuatChieuRequest request) {
        SuatChieu suatChieu = getSuatChieuById(id);
        Phim phim = phimRepository.findById(request.getMaPhim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        PhongChieu phongChieu = phongChieuRepository.findById(request.getMaPhongChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));
        suatChieu.setDonGiaCoSo(request.getDonGiaCoSo());
        suatChieu.setThoiGianBatDau(request.getThoiGianBatDau());
        suatChieu.setPhim(phim);
        suatChieu.setPhongChieu(phongChieu);
        return suatChieuRepository.save(suatChieu);
    }

    public void deleteSuatChieu(String id) {
        SuatChieu suatChieu = getSuatChieuById(id);
        suatChieuRepository.delete(suatChieu);
    }
}
