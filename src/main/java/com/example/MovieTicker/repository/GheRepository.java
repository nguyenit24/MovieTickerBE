package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.Ghe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GheRepository extends JpaRepository<Ghe, String> {
    boolean existsByTenGhe(String tenGhe);
}
