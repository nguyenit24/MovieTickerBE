package com.example.MovieTicker.request;

import lombok.Data;

@Data
public class DanhGiaPhimRequest {
    private Double rating;
    private String comment;
    private String phimId;
}
