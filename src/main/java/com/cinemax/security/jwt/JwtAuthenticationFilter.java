package com.cinemax.security.jwt;

import com.cinemax.security.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;

    private final UserDetailServiceImpl userDetailService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            //1-from every request, we will get JWT
            String jwt = parseJwt(request);
            //validate JWT
            if(jwt != null && jwtUtils.validateJwtToken(jwt)) {
                //3- we need email to get user information
                String email = jwtUtils.getEmailFromToken(jwt);//String username = jwtUtils.getEmailFromToken(jwt);
                //4- check DB and fetch user and upgrade it to userDetails
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                //5- set attribute with email
                request.setAttribute("email", email);
                //6- we load user details information to security context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e){
            LOGGER.error("Can not set user authentication", e);
        }
        filterChain.doFilter(request, response);
    }

    //Authorization -> Bearer ljsdfnkltskdfnvszlkfnvaqqdfknvaefkdsnvsacdfjknvcaldknsvcal
    private String parseJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/login")
                || path.startsWith("/api/register")
                || path.startsWith("/api/forgot-password")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

}
