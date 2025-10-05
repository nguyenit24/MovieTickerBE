package com.example.MovieTicker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MovieTicker.entity.PhongChieu;

public interface PhongChieuRepository extends JpaRepository<PhongChieu, String> {
	boolean existsByTenPhong(String tenPhong);
}
