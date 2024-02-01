package com.sandesh.overall.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String subject;

    public JwtAuthenticationToken(DecodedJWT decodedJWT) {
        super(AuthorityUtils.NO_AUTHORITIES);
        subject = decodedJWT.getSubject();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return super.getAuthorities();
    }

    @Override
    public Object getPrincipal() {
        return subject;
    }
}
