package com.unishare.backend.service;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unishare.backend.DTO.MovieRatingResponse;
import com.unishare.backend.DTO.MovieRequest;
import com.unishare.backend.DTO.MovieResponse;
import com.unishare.backend.DTO.ReviewResponse;
import com.unishare.backend.model.Movie;
import com.unishare.backend.model.User;
import com.unishare.backend.repository.MovieRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {
    final MovieRepository movieRepository;
    final AuthenticationService authenticationService;
    final ReviewService reviewService;
    private final String apiUrl = "http://localhost:9000";
    private final RestTemplate restTemplate;
    private ArrayList<MovieRatingResponse> movieRatingResponses;

    private MovieResponse convertToMovieResponse(Movie movie) {
        if (movie == null) return null;
        return MovieResponse.builder()
                .id(movie.getId())
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .genres(movie.getGenres())
                .imdbId(movie.getImdbId())
                .tmdbId(movie.getTmdbId())
                .year(movie.getYear())
                .build();
    }

    public MovieResponse addMovie(MovieRequest movieRequest) {
        Movie movie = Movie.builder()
                .movieId(movieRequest.getMovieId())
                .title(movieRequest.getTitle())
                .genres(movieRequest.getGenres())
                .imdbId(movieRequest.getImdbId())
                .tmdbId(movieRequest.getTmdbId())
                .year(movieRequest.getYear())
                .build();
        movieRepository.save(movie);
        return convertToMovieResponse(movie);
    }

    public MovieResponse getMovieByMovieId(Integer movieId) {
        Movie movie = movieRepository.findByMovieId(movieId);
        return convertToMovieResponse(movie);
    }

    public MovieResponse getMovieById(Integer id) {
        Movie movie = movieRepository.findById(id).orElseThrow();
        return convertToMovieResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        // only need first 10 movies
        return movies.stream().limit(10).map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByYear(String year) {
        List<Movie> movies = movieRepository.findByYear(year);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public void convertResponseString(String responseString, Integer userId) throws IOException {
        responseString = responseString.substring(3, responseString.length() - 4);
        movieRatingResponses = new ArrayList<>();

        String[] responseStringArray = responseString.split("], \\[");

//        List<MovieResponse> movieResponses = new ArrayList<>();
        for (String s : responseStringArray) {
            Integer movieId = Integer.parseInt(s.split(", ")[0]);

            MovieResponse movieResponse = getMovieByMovieId(movieId);
            if (movieResponse != null) {
//                movieResponses.add(movieResponse);
                movieRatingResponses.add(new MovieRatingResponse(movieId, Double.parseDouble(s.split(", ")[1])));
            }
        }

        List<ReviewResponse> reviewResponses = reviewService.getReviewsByUserId(userId);
        for (ReviewResponse reviewResponse : reviewResponses) {
            movieRatingResponses.add(new MovieRatingResponse(reviewResponse.getMovieId(), reviewResponse.getRating()));
        }

        System.out.println(movieRatingResponses.size());

        for (MovieRatingResponse movieRatingResponse : movieRatingResponses) {
            System.out.println(movieRatingResponse.getMovieId() + " " + movieRatingResponse.getRating());
        }

        return;
    }

    private void sortMovieRatingResponses() {
        movieRatingResponses.sort((o1, o2) -> {
            if (o1.getRating() < o2.getRating()) {
                return -1;
            } else if (o1.getRating() > o2.getRating()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public List<MovieResponse> getAllMoviesByUserId(String token) {
        movieRatingResponses = new ArrayList<>();
        User user = authenticationService.getMe(token);
        if (user.getId() == null) {
            throw new RuntimeException("User not found");
        }

        String api = apiUrl + "/getPredictionOutput/" + user.getId();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    api,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<String>() {
                    });

            String response = responseEntity.getBody();
            convertResponseString(response, user.getId());
            sortMovieRatingResponses();
            System.out.println(response);

            List<MovieResponse> movieResponses = new ArrayList<>();
            Set<Integer> movieIds = new HashSet<>();
            for (MovieRatingResponse movieRatingResponse : movieRatingResponses) {
                MovieResponse movieResponse = getMovieByMovieId(movieRatingResponse.getMovieId());
                if (movieResponse != null) {
                    if (movieIds.contains(movieResponse.getMovieId())) continue;
                    movieIds.add(movieResponse.getMovieId());
                    movieResponses.add(movieResponse);
                }
            }

            return movieResponses.stream().limit(10).toList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MovieResponse> getMoviesByTitleKeyWord(String title) {
        List<Movie> movies = movieRepository.findByTitleKeyWord(title);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByGenresKeyWord(String genres) {
        List<Movie> movies = movieRepository.findByGenresKeyWord(genres);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByGenresAndYear(String genres, String year) {
        List<Movie> movies = movieRepository.findByGenresAndYear(genres, year);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByTitleAndYear(String title, String year) {
        List<Movie> movies = movieRepository.findByTitleAndYear(title, year);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByTitleAndGenres(String title, String genres) {
        List<Movie> movies = movieRepository.findByTitleKeyWordAndGenresKeyWord(title, genres);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

    public List<MovieResponse> getMoviesByTitleAndGenresAndYear(String title, String genres, String year) {
        List<Movie> movies = movieRepository.findByTitleAndGenresAndYear(title, genres, year);
        return movies.stream().map(this::convertToMovieResponse).toList();
    }

}

