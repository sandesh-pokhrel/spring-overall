package com.sandesh.overall.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.length() > 7 && authorization.contains(" ")) {
            String token = authorization.split(" ")[1];
            DecodedJWT decodedJWT = JwtIssuer.verify(token);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            assert decodedJWT != null;
            securityContext.setAuthentication(new JwtAuthenticationToken(decodedJWT));
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
