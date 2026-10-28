package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.user.LoginRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.AuthResponseDTO;
import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.adapter.security.JwtService;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ){
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponseDTO register(UserRequestDTO dto){
        return userService.register(dto);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto){
        String email = dto.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        ));

        if (!passwordEncoder.matches(
                dto.password(),
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                    "Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getActive())){
            throw new BadCredentialsException(
                    "Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(token, "Bearer");
    }
}
