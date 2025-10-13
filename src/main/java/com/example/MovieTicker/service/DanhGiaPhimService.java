package com.example.MovieTicker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.MovieTicker.entity.DanhGiaPhim;
import com.example.MovieTicker.entity.TaiKhoan;
import com.example.MovieTicker.repository.DanhGiaPhimRepository;
import com.example.MovieTicker.repository.TaiKhoanRepository;
import com.example.MovieTicker.request.DanhGiaPhimRequest;
import com.example.MovieTicker.response.DanhGiaPhimResponse;

@Service
public class DanhGiaPhimService {

    @Autowired
    private DanhGiaPhimRepository danhGiaPhimRepository;

    @Autowired
    private PhimService phimService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    public DanhGiaPhimResponse saveDanhGiaPhim(DanhGiaPhimRequest danhGiaPhim) {
        DanhGiaPhim entity = new DanhGiaPhim();
        entity.setRating(danhGiaPhim.getRating());
        entity.setComment(danhGiaPhim.getComment());
        entity.setPhim(phimService.getPhimById(danhGiaPhim.getPhimId()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = taiKhoanRepository.findById(authentication.getName()).orElse(null);
        entity.setUser(taiKhoan.getUser());
        entity = danhGiaPhimRepository.save(entity);
        DanhGiaPhimResponse response = new DanhGiaPhimResponse();
        response.setRating(entity.getRating());
        response.setComment(entity.getComment());
        response.setTenNguoiDung(entity.getUser().getHoTen());
        response.setNgayDanhGia(entity.getCreatedAt());
        return response;
    }

    public void deleteDanhGiaPhim(String id) {
        danhGiaPhimRepository.deleteById(id);
    }

    public List<DanhGiaPhimResponse> getDanhGiaByPhimId(String maPhim) {
        List<DanhGiaPhim> danhGiaPhims = danhGiaPhimRepository.findByPhimMaPhim(maPhim);
        return danhGiaPhims.stream().map(dg -> {
            DanhGiaPhimResponse response = new DanhGiaPhimResponse();
            response.setRating(dg.getRating());
            response.setComment(dg.getComment());
            response.setTenNguoiDung(dg.getUser().getHoTen());
            response.setNgayDanhGia(dg.getCreatedAt());
            return response;
        }).toList();
    }
    public DanhGiaPhimResponse getDanhGiaPhimById(String id) {
        DanhGiaPhim entity = danhGiaPhimRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        DanhGiaPhimResponse response = new DanhGiaPhimResponse();
        response.setRating(entity.getRating());
        response.setComment(entity.getComment());
        response.setTenNguoiDung(entity.getUser().getHoTen());
        response.setNgayDanhGia(entity.getCreatedAt());
        return response;
    }

    public List<DanhGiaPhimResponse> getAllDanhGiaPhim() {
        List<DanhGiaPhim> danhGiaPhims = danhGiaPhimRepository.findAll();
        return danhGiaPhims.stream().map(dg -> {
            DanhGiaPhimResponse response = new DanhGiaPhimResponse();
            response.setRating(dg.getRating());
            response.setComment(dg.getComment());
            response.setTenNguoiDung(dg.getUser().getHoTen());
            response.setNgayDanhGia(dg.getCreatedAt());
            return response;
        }).toList();
    }

}
