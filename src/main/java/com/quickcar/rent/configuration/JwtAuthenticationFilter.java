package com.quickcar.rent.configuration;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.quickcar.rent.service.jwt.UserService;
import com.quickcar.rent.utils.JWTUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtHelper;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwt;

        //System.out.println("Authorization Header: " + authHeader); // Debug statement

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
           // System.out.println("No JWT token found or header is malformed."); // Debug statement
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtHelper.extractUserName(jwt);
        //System.out.println("Extracted User Email: " + userEmail); // Debug statement

        if (StringUtils.hasText(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
            //System.out.println("Loaded User Details: " + userDetails); // Debug statement

            if (jwtHelper.isTokenValid(jwt, userDetails)) {
                Claims claims = jwtHelper.extractAllClaims(jwt);
                List<String> roles = claims.get("roles", List.class);
                //System.out.println("Roles from Token: " + roles); // Debug statement

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //System.out.println("Authentication set in SecurityContext."); // Debug statement
            } else {
               // System.out.println("Invalid Token."); // Debug statement
            }
        }

        filterChain.doFilter(request, response);
    }

}
