package com.unishare.backend.controller;

import com.unishare.backend.DTO.MovieRequest;
import com.unishare.backend.DTO.MovieResponse;
import com.unishare.backend.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {
    final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("year/{year}")
    public ResponseEntity<List<MovieResponse>> getMoviesByYear(
            @PathVariable String year
    ) {
        return ResponseEntity.ok(movieService.getMoviesByYear(year));
    }

    @GetMapping("movie-id/{movieId}")
    public ResponseEntity<MovieResponse> getMovieByMovieId(
            @PathVariable Integer movieId
    ) {
        return ResponseEntity.ok(movieService.getMovieByMovieId(movieId));
    }

    @PostMapping
    public ResponseEntity<MovieResponse> addMovie(
            @RequestBody MovieRequest request
    ) {
        return ResponseEntity.ok(movieService.addMovie(request));
    }

    @GetMapping("/user")
    public ResponseEntity<List<MovieResponse>> getAllMoviesByUserId(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesByUserId(token));
    }

    // query params
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> getAllMoviesByUserId(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) String year
    ) {
        System.out.println("(" + title + "|" + genres + "|" + year + ")");
        if (title != "" && genres != "" && year != "") {
            return ResponseEntity.ok(movieService.getMoviesByTitleAndGenresAndYear(title, genres, year));
        } else if (title != "" && genres != "") {
            return ResponseEntity.ok(movieService.getMoviesByTitleAndGenres(title, genres));
        } else if (title != "" && year != "") {
            return ResponseEntity.ok(movieService.getMoviesByTitleAndYear(title, year));
        } else if (genres != "" && year != "") {
            return ResponseEntity.ok(movieService.getMoviesByGenresAndYear(genres, year));
        } else if (title != "") {
            return ResponseEntity.ok(movieService.getMoviesByTitleKeyWord(title));
        } else if (genres != "") {
            return ResponseEntity.ok(movieService.getMoviesByGenresKeyWord(genres));
        } else if (year != "") {
            return ResponseEntity.ok(movieService.getMoviesByYear(year));
        } else {
            return ResponseEntity.ok(movieService.getAllMovies());
        }
    }
}
