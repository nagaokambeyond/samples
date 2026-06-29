package com.example.demo.api.controller;

import com.example.demo.api.AuthOperationApi;
import com.example.demo.api.request.LoginRequest;
import com.example.demo.api.response.LoginResponse;
import com.example.demo.config.JwtTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class AuthOperationApiController implements AuthOperationApi {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResponse login(@Valid @NotNull LoginRequest request) {
        final var authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(), request.getPassword())
        );
        final var response = new LoginResponse();
        response.setUsername(authentication.getName());
        response.setTokenType("Bearer");
        response.setAccessToken(jwtTokenService.createToken(authentication));
        response.setExpiresIn(jwtTokenService.getExpiresInSeconds());
        return response;
    }
}
