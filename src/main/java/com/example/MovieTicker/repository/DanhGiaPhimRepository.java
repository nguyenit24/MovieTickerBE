package com.example.MovieTicker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MovieTicker.entity.DanhGiaPhim;

@Repository
public interface DanhGiaPhimRepository extends JpaRepository<DanhGiaPhim, String> {
    List<DanhGiaPhim> findByPhimMaPhim(String maPhim);
}
