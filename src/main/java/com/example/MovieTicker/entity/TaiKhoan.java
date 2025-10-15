package com.example.MovieTicker.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TaiKhoan")
@ToString(exclude = "user")
public class TaiKhoan {
    @Id
    private String TenDangNhap;
    
    private String MatKhau;

    @ManyToOne
    @JoinColumn(name = "maUser", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "vaiTroID", nullable = false)
    @JsonIgnore
    private VaiTro vaiTro;

}
