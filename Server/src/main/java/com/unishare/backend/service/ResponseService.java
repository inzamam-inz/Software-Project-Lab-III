package com.unishare.backend.service;

import com.unishare.backend.DTO.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseService {
    public Response getResponse(String message) {
        return new Response(message);
    }
}
