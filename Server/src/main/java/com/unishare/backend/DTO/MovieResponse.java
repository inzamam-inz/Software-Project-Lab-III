package com.unishare.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    private Integer id;
    private Integer movieId;
    private String title;
    private String genres;
    private String imdbId;
    private String tmdbId;
    private String year;
}
