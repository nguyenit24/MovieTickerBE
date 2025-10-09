package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.KhuyenMai;
import com.example.MovieTicker.repository.KhuyenMaiRepository;
import com.example.MovieTicker.request.KhuyenMaiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class KhuyenMaiService {
    
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    
    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiRepository.findAll();
    }
    
    public List<KhuyenMai> getValidPromotions() {
        return khuyenMaiRepository.findValidPromotions(LocalDate.now());
    }
    
    public Optional<KhuyenMai> getKhuyenMaiById(String id) {
        return khuyenMaiRepository.findById(id);
    }
    
    public List<KhuyenMai> searchKhuyenMai(String keyword) {
        return khuyenMaiRepository.findByKeyword(keyword);
    }
    
    public KhuyenMai createKhuyenMai(KhuyenMaiRequest request) {
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }   

        KhuyenMai khuyenMai = KhuyenMai.builder()
                .tenKm(request.getTenKm())
                .moTa(request.getMoTa())
                .giaTri(request.getGiaTri())
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .build();
        
        return khuyenMaiRepository.save(khuyenMai);
    }
    
    public KhuyenMai updateKhuyenMai(String id, KhuyenMaiRequest request) {
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(id);
        if (optionalKhuyenMai.isPresent()) {
            if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
                throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
            }
            KhuyenMai khuyenMai = optionalKhuyenMai.get();
            khuyenMai.setTenKm(request.getTenKm());
            khuyenMai.setMoTa(request.getMoTa());
            khuyenMai.setGiaTri(request.getGiaTri());
            khuyenMai.setNgayBatDau(request.getNgayBatDau());
            khuyenMai.setNgayKetThuc(request.getNgayKetThuc());
            
            return khuyenMaiRepository.save(khuyenMai);
        }
        return null;
    }
    
    public boolean deleteKhuyenMai(String id) {
        if (khuyenMaiRepository.existsById(id)) {
            khuyenMaiRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Page<KhuyenMai> getKhuyenMaiPage(Pageable pageable) {
        return khuyenMaiRepository.findAll(pageable);
    }
}