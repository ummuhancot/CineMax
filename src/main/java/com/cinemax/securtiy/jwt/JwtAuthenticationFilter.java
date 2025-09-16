package com.cinemax.securtiy.jwt;

import com.cinemax.securtiy.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// for extra information please read
//https://www.baeldung.com/spring-onceperrequestfilter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //1-from every request, we will get JWT
            String jwt = parseJwt(request);
            //validate JWT
            if(jwt != null && jwtUtils.validateToken(jwt)) {
                //3- we need username to get user information
                String username = jwtUtils.getUsernameFromToken(jwt);
                //4- check DB and fetch user and upgrade it to userDetails
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                //5- set attribute with username
                request.setAttribute("username", username);
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
}
