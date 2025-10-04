package com.example.MovieTicker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.MovieTicker.entity.Phim;

@Repository
public interface PhimRepository extends JpaRepository<Phim, String> {
   
    Optional<Phim> findByTenPhim(String tenPhim);
    
    boolean existsByTenPhim(String tenPhim);
    
    
    @Query("SELECT p FROM Phim p WHERE LOWER(p.tenPhim) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Phim> findByTenPhimContainingIgnoreCase(@Param("keyword") String keyword);
    
}