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

/**
 * Filtro de segurança responsável por autenticar requisições
 * que apresentam um token JWT válido no cabeçalho Authorization.
 *
 * <p>Consulta o usuário associado ao token, verifica se está ativo
 * e registra sua autenticação no contexto de segurança do Spring.</p>
 *
 * @author Maria Luiza
 */
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

    /**
     * Processa a requisição para verificar o token JWT e configurar
     * o contexto de autenticação quando as validações são satisfeitas.
     *
     * <p>Requisições sem o cabeçalho Bearer seguem a cadeia de filtros
     * sem autenticação realizada por este filtro. Tokens inválidos ou
     * associados a usuários inativos não estabelecem autenticação.</p>
     *
     * @param request requisição HTTP recebida
     * @param response resposta HTTP
     * @param filterChain cadeia de filtros de segurança
     * @throws ServletException se ocorrer um erro no processamento do filtro
     * @throws IOException se ocorrer um erro de entrada ou saída
     */
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
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.isTokenValid(token, user.getEmail())){
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            var authorities = List.of(
                    new SimpleGrantedAuthority(
                            "ROLE_" + user.getRole().name()
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

    /**
     * Define os endpoints de autenticação que não precisam passar
     * pelo processamento deste filtro JWT.
     *
     * @param request requisição HTTP recebida
     * @return {@code true} para caminhos iniciados por /api/auth/
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth/");
    }
}
