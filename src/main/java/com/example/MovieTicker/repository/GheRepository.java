package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.Ghe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface GheRepository extends JpaRepository<Ghe, String> {
    boolean existsByTenGhe(String tenGhe);
    
    @Override
    @NonNull
    Page<Ghe> findAll(@NonNull Pageable pageable);
}
