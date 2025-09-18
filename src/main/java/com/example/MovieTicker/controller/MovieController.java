package com.example.MovieTicker.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MovieTicker.model.Movie;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @GetMapping
    public List<Movie> getAllMovies() {
         return Arrays.asList(
            new Movie(11816, "MƯA ĐỎ", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11816_103_100004.jpg", "전체", "124Phút", "22/08/2025"),
            new Movie(11866, "KHẾ ƯỚC BÁN DÂU", "청불", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202509/11866_103_100003.jpg", "청불", "118Phút", "12/09/2025"),
            new Movie(11871, "THE CONJURING: NGHI LỄ CUỐI CÙNG", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11871_103_100002.jpg", "전체", "135Phút", "12/09/2025"),
            new Movie(11855, "LÀM GIÀU VỚI MA: CUỘC CHIẾN HỘT XOÀN", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11855_103_100003.jpg", "전체", "126Phút", "29/08/2025"),
            new Movie(11647, "BĂNG ĐẢNG QUÁI KIỆT 2", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11647_103_100003.jpg", "전체", "104Phút", "29/08/2025"),
            new Movie(11882, "PHIM SHIN CẬU BÉ BÚT CHÌ", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11882_103_100001.jpg", "전체", "105Phút", "22/08/2025"),
            new Movie(11862, "THANH GƯƠM DIỆT QUỶ: VÔ HẠN THÀNH", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11862_103_100002.jpg", "전체", "155Phút", "15/08/2025"),
            new Movie(11842, "CÔ DÂU MA", "전체", "https://media.lottecinemavn.com/Media/MovieFile/MovieImg/202508/11842_103_100003.jpg", "전체", "96Phút", "05/09/2025")
        );
    }
}
