package com.unishare.backend.repository;

import com.unishare.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserId(Integer userId);
    List<Review> findByMovieId(Integer movieId);
    List<Review> findByIsAddedToModel(Boolean isAddedToModel);
    List<Review> findByUserIdAndMovieId(Integer userId, Integer movieId);

}
