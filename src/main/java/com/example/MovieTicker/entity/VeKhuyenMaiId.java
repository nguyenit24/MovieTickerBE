package com.example.MovieTicker.entity;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VeKhuyenMaiId implements Serializable {
    private Long maVe;
    private String maKm; 
}