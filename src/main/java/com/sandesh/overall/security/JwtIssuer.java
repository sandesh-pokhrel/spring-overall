package com.sandesh.overall.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sandesh.overall.model.LoginRequest;
import com.sandesh.overall.model.LoginResponse;

public class JwtIssuer {

    private static final String SECRET_KEY = "fake_unsecured_hmac512_secret_key";
    private static final String ISSUER = "spring_overall";

    public static LoginResponse issue(LoginRequest loginRequest) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET_KEY);
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(loginRequest.username())
                    .withClaim("username", loginRequest.username())
                    .sign(algorithm);
            return new LoginResponse(token);
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
            return null;
        }
    }

    public static DecodedJWT verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            return null;
        }
    }
}
