package com.unishare.backend.controller;

import com.unishare.backend.DTO.AuthenticationRequest;
import com.unishare.backend.DTO.AuthenticationResponse;
import com.unishare.backend.DTO.RegisterRequest;
import com.unishare.backend.DTO.Response;
import com.unishare.backend.service.AuthenticationService;
import com.unishare.backend.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/rebuilt-rs")
    public ResponseEntity<Response> builtRS(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(authenticationService.builtRS(token));
    }
}

