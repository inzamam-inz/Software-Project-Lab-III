package com.unishare.backend.repository;

import com.unishare.backend.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie findByMovieId(Integer movieId);
    List <Movie> findByYear(String year);
    @Query(value = "SELECT * FROM movie WHERE title LIKE %:title%", nativeQuery = true)
    List<Movie> findByTitleKeyWord(@Param("title") String title);

    @Query(value = "SELECT * FROM movie WHERE genres LIKE %:genres%", nativeQuery = true)
    List<Movie> findByGenresKeyWord(@Param("genres") String genres);

    @Query(value = "SELECT * FROM movie WHERE genres LIKE %:genres% AND year = :year", nativeQuery = true)
    List<Movie> findByGenresAndYear(@Param("genres") String genres, @Param("year") String year);

    @Query(value = "SELECT * FROM movie WHERE title LIKE %:title% AND year = :year", nativeQuery = true)
    List<Movie> findByTitleAndYear(@Param("title") String title, @Param("year") String year);

    @Query(value = "SELECT * FROM movie WHERE title LIKE %:title% AND genres LIKE %:genres%", nativeQuery = true)
    List<Movie> findByTitleKeyWordAndGenresKeyWord(@Param("title") String title, @Param("genres") String genres);

    @Query(value = "SELECT * FROM movie WHERE title LIKE %:title% AND genres LIKE %:genres% AND year = :year", nativeQuery = true)
    List<Movie> findByTitleAndGenresAndYear(@Param("title") String title, @Param("genres") String genres, @Param("year") String year);
}
