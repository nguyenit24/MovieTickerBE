package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.LoaiGhe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiGheRepository extends JpaRepository<LoaiGhe, String> {
    boolean existsByTenLoaiGhe(String tenLoaiGhe);
    LoaiGhe findByTenLoaiGhe(String tenLoaiGhe);
    
    @Override
    @NonNull
    Page<LoaiGhe> findAll(@NonNull Pageable pageable);
}
