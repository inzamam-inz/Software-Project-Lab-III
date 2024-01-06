package com.unishare.backend.controller;


import com.unishare.backend.DTO.ReviewRequest;
import com.unishare.backend.DTO.ReviewResponse;
import com.unishare.backend.model.Review;
import com.unishare.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    final ReviewService reviewService;

    @GetMapping()
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PostMapping()
    public ResponseEntity<ReviewResponse> addReview(
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(reviewService.addReview(token, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ReviewResponse> getReviewsByMovieId(
            @PathVariable Integer movieId,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(reviewService.getReviewByUserAndMovieId(token, movieId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReviewByMovieId(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token,
            @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, token, request));
    }
}
