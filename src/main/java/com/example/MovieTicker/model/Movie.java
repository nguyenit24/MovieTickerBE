package com.example.MovieTicker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
// cái này dùng test chứ model không dùng
@Getter
@Setter
@AllArgsConstructor
public class Movie {
    private int id;
    private String title;
    private String genre;
    private String img;
    private String rating;
    private String duration;
    private String releaseDate;
}
