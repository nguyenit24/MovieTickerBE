package com.example.MovieTicker.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeKhuyenMaiId implements Serializable {
    private Long maVe;
    private Long maKm;
}