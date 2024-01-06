package com.unishare.backend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.unishare.backend.DTO.*;
import com.unishare.backend.config.JwtService;
import com.unishare.backend.exceptionHandlers.ErrorMessageException;
import com.unishare.backend.model.*;
import com.unishare.backend.repository.ReviewRepository;
import com.unishare.backend.repository.TokenRepository;
import com.unishare.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ReviewRepository reviewRepository;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ResponseService responseService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (!repository.findByEmail(request.getEmail()).isEmpty()) {
            System.out.println("Email already exists");
            System.out.println(repository.findByEmail(request.getEmail()));
            throw new ErrorMessageException("Email already exists");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public User getMe(String token) {
        token = token.substring(7);
        String email = jwtService.extractUsername(token);
        return repository.findByEmail(email)
                .orElseThrow(() -> new ErrorMessageException("User not found with email: " + email));
    }

    public Response builtRS(String token) {
        User user = getMe(token);

        if (user.getEmail().equals("bsse1113@iit.du.ac.bd")) {
            if (addReviewToDataset()) {
                final String apiURL = "http://localhost:9000/model";

                try {
                    RestTemplate restTemplate = new RestTemplate();
                    String result = restTemplate.getForObject(apiURL, String.class);
                    return responseService.getResponse(result);
                } catch (Exception e) {
                    return responseService.getResponse("Error");
                }
            }
            return responseService.getResponse("Dataset is empty");
        }
        else {
            return responseService.getResponse("You are not allowed to access this resource.");
        }
    }

    public Boolean addReviewToDataset() {
        List<Review> reviews = reviewRepository.findByIsAddedToModel(false);
        if (reviews.isEmpty()) {
            return false;
        }
        System.out.println(reviews.size());

        // convert to json
        List<ReviewJson> reviewJsons = reviews.stream().map(review -> new ReviewJson(
                review.getUser().getId(),
                review.getMovieId(),
                review.getRating(),
                review.getCreatedAt()
        )).collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the list to a JSON array
            String jsonArray = objectMapper.writeValueAsString(reviewJsons);

            // post to api
            try {
                String apiURL = "http://localhost:9000/adduserfeedback";
                RestTemplate restTemplate = new RestTemplate();

                // Set the request headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Set the request body as a string
                String requestBody = jsonArray;

                // Create the HTTP entity with headers and body
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                // Make the POST request
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL, requestEntity, String.class);


//                ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL, jsonArray, String.class);
                String result = responseEntity.getBody();
                System.out.println(result);
            }
            catch (Exception e) {
                return false;
            }


            for (Review review : reviews) {
                review.setIsAddedToModel(true);
                reviewRepository.save(review);
            }
            // Print the JSON array
            System.out.println("JSON array representation: " + jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
