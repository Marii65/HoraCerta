package com.HoraCerta.horacerta.adapter.security;

import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository){
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected  void  doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);

            if (email == null || SecurityContextHolder.getContext()
                    .getAuthentication() != null){
                filterChain.doFilter(request, response);
                return;
            }
            User user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user == null || !Boolean.TRUE.equals(user.getActive())){
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.isTokenValid(token, user.getEmail())){
                filterChain.doFilter(request, response);
                return;
            }

            var authorities = List.of(
                    new SimpleGrantedAuthority(
                            "ROLE_USER" + user.getRole().name()
                    )
            );

            var authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    authorities
            );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException ex){
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
