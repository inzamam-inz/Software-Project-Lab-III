package com.unishare.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unishare.backend.DTO.ReviewJson;
import com.unishare.backend.DTO.ReviewRequest;
import com.unishare.backend.DTO.ReviewResponse;
import com.unishare.backend.exceptionHandlers.ErrorMessageException;
import com.unishare.backend.model.Review;
import com.unishare.backend.model.User;
import com.unishare.backend.repository.ReviewRepository;

import com.unishare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    final ReviewRepository reviewRepository;
    final UserRepository userRepository;
    final AuthenticationService authenticationService;

    public ReviewResponse makeResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getMovieId(),
                review.getRating(),
                review.getIsAddedToModel(),
                review.getCreatedAt().toString()
        );
    }

    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(this::makeResponse).collect(Collectors.toList());
    }

    public ReviewResponse getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Review not found with ID: " + id));
        return makeResponse(review);
    }

    public void deleteReview(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Review not found with ID: " + id));
        reviewRepository.delete(review);
    }

    public ReviewResponse addReview(String token, ReviewRequest reviewRequest) {
        User user = authenticationService.getMe(token);

        Review review = new Review();
        review.setUser(userRepository.findById(user.getId())
                .orElseThrow(() -> new ErrorMessageException("User not found with ID: " + user.getId())));
        review.setMovieId(reviewRequest.getMovieId());
        review.setRating(reviewRequest.getRating());
        review.setIsAddedToModel(false);
        review.setCreatedAt(new Date());

        reviewRepository.save(review);
        return makeResponse(review);
    }

    public List<ReviewResponse> getReviewsByUserId(Integer userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream().map(this::makeResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByMovieId(Integer movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return reviews.stream().map(this::makeResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByIsAddedToModel(Boolean isAddedToModel) {
        List<Review> reviews = reviewRepository.findByIsAddedToModel(isAddedToModel);
        return reviews.stream().map(this::makeResponse).collect(Collectors.toList());
    }

    public String addToDataset() {
        List<Review> reviews = reviewRepository.findByIsAddedToModel(false);
        for (Review review : reviews) {
            review.setIsAddedToModel(true);
            reviewRepository.save(review);
        }
        return "Reviews added to dataset";
    }

    public ReviewResponse updateReview(Integer id, String token, ReviewRequest reviewRequest) {
        User user = authenticationService.getMe(token);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("Review not found with ID: " + id));
        review.setRating(reviewRequest.getRating());
        review.setIsAddedToModel(false);
        review.setCreatedAt(new Date());

        reviewRepository.save(review);
        return makeResponse(review);
    }

    public ReviewResponse getReviewByUserAndMovieId(String token, Integer movieId) {
        Integer userId = authenticationService.getMe(token).getId();
        List<Review> reviews = reviewRepository.findByUserIdAndMovieId(userId, movieId);
        if (reviews.isEmpty()) {
            return new ReviewResponse();
        }
        return makeResponse(reviews.get(reviews.size() - 1));
    }

    public List<ReviewResponse> getUserReviews(String token) {
        Integer userId = authenticationService.getMe(token).getId();
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream().map(this::makeResponse).collect(Collectors.toList());
    }

//    public Boolean addReviewToDataset() {
//        List<Review> reviews = reviewRepository.findByIsAddedToModel(false);
//
//        // convert to json
//        List<ReviewJson> reviewJsons = reviews.stream().map(review -> new ReviewJson(
//                review.getUser().getId(),
//                review.getMovieId(),
//                review.getRating(),
//                review.getCreatedAt()
//        )).collect(Collectors.toList());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            // Convert the list to a JSON array
//            String jsonArray = objectMapper.writeValueAsString(reviewJsons);
//            for (Review review : reviews) {
//                review.setIsAddedToModel(true);
//                reviewRepository.save(review);
//            }
//            // Print the JSON array
//            System.out.println("JSON array representation: " + jsonArray);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//        return true;
//    }
}
