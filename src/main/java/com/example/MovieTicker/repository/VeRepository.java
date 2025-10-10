package com.example.MovieTicker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.MovieTicker.entity.Ve;

import feign.Param;

public interface VeRepository extends JpaRepository<Ve, String> {
    List<Ve> findBySuatChieuMaSuatChieuAndGheMaGheIn(String maSc, List<String> maGheList);
    @Query("SELECT v FROM Ve v WHERE v.suatChieu.maSuatChieu = :maSuatChieu")
    List<Ve> findBySuatChieuMaSuatChieu(@Param("maSuatChieu") String maSuatChieu);
}
