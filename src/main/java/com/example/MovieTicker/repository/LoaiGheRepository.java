package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.LoaiGhe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiGheRepository extends JpaRepository<LoaiGhe, String> {
    boolean existsByTenLoaiGhe(String tenLoaiGhe);
    LoaiGhe findByTenLoaiGhe(String tenLoaiGhe);
}
