package com.example.MovieTicker.repository;

import com.example.MovieTicker.entity.CauHinhHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<CauHinhHeThong, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(c.maCauHinh, 8) AS int)) FROM CauHinhHeThong c WHERE c.maCauHinh LIKE 'SLIDER%'")
    Integer findMaxSliderNumber();

}
