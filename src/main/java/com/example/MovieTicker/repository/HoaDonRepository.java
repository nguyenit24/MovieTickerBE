package com.example.MovieTicker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MovieTicker.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    
}
