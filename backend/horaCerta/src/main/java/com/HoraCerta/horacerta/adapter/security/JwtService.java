package com.HoraCerta.horacerta.adapter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Serviço responsável pela geração e validação de tokens JWT
 * utilizados na autenticação da aplicação.
 *
 * <p>A chave de assinatura e o tempo de expiração são definidos
 * por propriedades externas da aplicação.</p>
 *
 * @author Maria Luiza
 */
@Service
public class JwtService {
    private final SecretKey signinKey;
    private final Long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        if (keyBytes.length < 32){
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 bytes."
            );
        }

        if (expirationMs <= 0) {
            throw new IllegalArgumentException(
                    "JWT expiration must be greater than zero."
            );
        }

        this.signinKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;

    }

    /**
     * Gera um token JWT assinado para o usuário identificado pelo e-mail.
     *
     * @param email e-mail utilizado como identificador do usuário no token
     * @return token JWT assinado, com datas de emissão e expiração
     */
    public String generateToken(String email){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signinKey)
                .compact();
    }

    /**
     * Extrai o identificador do usuário registrado no token.
     *
     * @param token token JWT que será analisado
     * @return identificador do usuário presente no campo subject
     * @throws JwtException se o token não puder ser validado ou analisado
     */
    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    /**
     * Verifica se o token corresponde ao e-mail esperado e se
     * possui uma data de expiração válida.
     *
     * @param token token JWT a ser validado
     * @param expectedEmail e-mail esperado para o usuário
     * @return {@code true} se as verificações forem satisfeitas;
     *         {@code false} caso contrário
     */
    public boolean isTokenValid(String token, String expectedEmail){
        try {
            Claims claims = extractClaims(token);

            return expectedEmail != null
                    && expectedEmail.equalsIgnoreCase(claims.getSubject())
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex){
            return false;
        }
    }

    /**
     * Extrai as informações de um token JWT após verificar sua assinatura.
     *
     * @param token token JWT que será analisado
     * @return informações contidas no corpo do token
     * @throws JwtException se a assinatura ou a estrutura do token for inválida
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signinKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
