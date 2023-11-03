package dev.saif.userservice.controllers;

import dev.saif.userservice.dtos.*;
import dev.saif.userservice.models.SessionStatus;
import dev.saif.userservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto signupRequest) {
        return authService.signUp(signupRequest.getEmail(), signupRequest.getPassword());
    }

//    @PostMapping("/login")
//    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequest) {
//        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequest) {
        return authService.logout(logoutRequest.getToken(), logoutRequest.getUserId());
    }

    @PostMapping("/validateToken")
    public SessionStatus validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequest) {
        return authService.validateToken(validateTokenRequest.getUserId(), validateTokenRequest.getToken());
    }   
}
