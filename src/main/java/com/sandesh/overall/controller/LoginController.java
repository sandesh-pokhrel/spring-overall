package com.sandesh.overall.controller;

import com.sandesh.overall.model.LoginRequest;
import com.sandesh.overall.model.LoginResponse;
import com.sandesh.overall.security.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private static final String FAKE_USER = "username";
    private static final String FAKE_PASSWORD = "password";

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.username().equals(FAKE_USER) && loginRequest.password().equals(FAKE_PASSWORD)) {
            return JwtIssuer.issue(loginRequest);
        }
        return null;
    }
}
