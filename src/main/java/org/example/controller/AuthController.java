package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.CreateUserRequest;
import org.example.dto.ApiDtos.LoginRequest;
import org.example.dto.ApiDtos.LoginResponse;
import org.example.dto.ApiDtos.UserResponse;
import org.example.security.AppUserService;
import org.example.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AppUserService appUserService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        TokenService.IssuedToken issued = tokenService.issue(authentication);
        return new LoginResponse(issued.token(), "Bearer", issued.expiresAt());
    }

    /** Admin-only: registers a new staff user (ADMIN or EMPLOYEE). */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return UserResponse.from(appUserService.createUser(request.username(), request.password(), request.role()));
    }
}
