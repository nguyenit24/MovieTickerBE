package com.example.MovieTicker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CauHinhHeThong implements Serializable {

    @Id
    private String maCauHinh;

    @Column(nullable = false, unique = true)
    private String tenCauHinh;

    @Column(nullable = false)
    private String giaTri;

    @Column(nullable = false)
    private String loai;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime ngayCapNhat;
}
