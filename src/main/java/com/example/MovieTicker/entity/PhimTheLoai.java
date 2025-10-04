package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Phim_TheLoai")
public class PhimTheLoai {
    @EmbeddedId
    private PhimTheLoaiComposkey id;

    @ManyToOne
    @MapsId("MaPhim")
    @JoinColumn(name = "MaPhim")
    private Phim phim;

    @ManyToOne
    @MapsId("MaTheLoai")
    @JoinColumn(name = "MaTheLoai")
    private TheLoaiPhim theLoaiPhim;


}
