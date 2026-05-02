package com.example.demo.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {
    private static final Logger JWT_LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip JWT validation for public endpoints
        if (uri.equals("/") || uri.startsWith("/css/") || uri.startsWith("/js/") || 
            uri.startsWith("/views/") || uri.endsWith(".html") || uri.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                try {
                    Claims c = jwtUtil.parseToken(token);
                    String username = c.getSubject();
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } catch (UsernameNotFoundException e) {
                        JWT_LOGGER.debug("JWT subject not found (user may have been deleted): {}", username);
                    } catch (Exception e) {
                        JWT_LOGGER.warn("Error loading user details for JWT subject {}: {}", username, e.getMessage());
                    }
                } catch (Exception e) {
                    JWT_LOGGER.debug("Failed to parse/validate JWT token: {}", e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}