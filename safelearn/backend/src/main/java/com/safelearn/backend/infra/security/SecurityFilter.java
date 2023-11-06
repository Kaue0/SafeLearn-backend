package com.safelearn.backend.infra.security;

import com.safelearn.backend.repository.UserRepository;
import com.safelearn.backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = retrieveToken(httpServletRequest);
        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var user = repository.findById(subject);
            var authentication = new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String retrieveToken(HttpServletRequest httpServletRequest) {
        var authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null) {
            String header = authorizationHeader.replace("Bearer ", "");
            return header;
        } else {
            return null;
        }
    }
}
