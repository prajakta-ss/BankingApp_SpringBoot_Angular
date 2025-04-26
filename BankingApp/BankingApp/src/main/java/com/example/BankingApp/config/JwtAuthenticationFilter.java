package com.example.BankingApp.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.BankingApp.service.CustomUserDetailsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	 @Value("${jwt.secret}")
	    private String jwtSecret;

	    private final UserDetailsService userDetailsService;

	    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService) {
	        this.userDetailsService = userDetailsService;
	    }
	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {

	        String authHeader = request.getHeader("Authorization");

	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            // No token, allow request to continue or handle accordingly
	            filterChain.doFilter(request, response);
	            return;
	        }

	        String token = authHeader.substring(7);  // Extract token

	        try {
	            JwtParser jwtParser = Jwts.parserBuilder()
	                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
	                    .build();

	            String username = jwtParser.parseClaimsJws(token)
	                    .getBody()
	                    .getSubject();

	            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                        userDetails, null, userDetails.getAuthorities());
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }

	        } catch (JwtException e) {
	            logger.error("Invalid JWT token: {}", e.getMessage());
	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
	            return;
	        }

	        filterChain.doFilter(request, response);
	    }


	    
	   	    @Override
	    protected boolean shouldNotFilter(HttpServletRequest request) {
	        String path = request.getRequestURI();
	        return path.equals("/api/accounts/login") || path.equals("/api/accounts/register");
	    }


}