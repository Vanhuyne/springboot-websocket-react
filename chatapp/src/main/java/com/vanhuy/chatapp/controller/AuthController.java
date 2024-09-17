package com.vanhuy.chatapp.controller;

import com.vanhuy.chatapp.dto.LoginRequest;
import com.vanhuy.chatapp.dto.LoginResponse;
import com.vanhuy.chatapp.dto.RegisterDTO;
import com.vanhuy.chatapp.model.User;
import com.vanhuy.chatapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000/")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            String token = authService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token , user.getUsername()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterDTO user) {

        User newUser = authService.register(user);

        return ResponseEntity.ok(newUser);
    }
}
