package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.SuatChieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuatChieuRepository extends JpaRepository<SuatChieu, String> {
}
