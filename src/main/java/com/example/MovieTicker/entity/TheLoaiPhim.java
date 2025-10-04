package com.example.MovieTicker.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class TheLoaiPhim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maTheLoai;

    @Column(length = 255, unique = true, nullable = false)
    private String tenTheLoai;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "Phim_TheLoai",
            joinColumns = @JoinColumn(name = "MaPhim"),
            inverseJoinColumns = @JoinColumn(name = "MaTheLoai")
    )
    private List<Phim> listPhim = new ArrayList<>();

    @PreRemove
    public void preRemove(){
        for (Phim phim : listPhim) {
            phim.getListTheLoai().remove(this);
        }
    }
}
