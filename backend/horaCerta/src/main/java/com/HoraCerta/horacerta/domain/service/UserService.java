package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.user.UserRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserRoleUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.exception.EmailAlreadyExistsException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Serviço responsável pelo cadastro, consulta, atualização e exclusão
 * de usuários, incluindo o gerenciamento de seus níveis de acesso.
 *
 * <p>Também realiza a normalização dos e-mails e a codificação das
 * senhas antes de persistir os dados.</p>
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cadastra um novo usuário com o perfil padrão de operador.
     *
     * @param dto dados necessários para o cadastro
     * @return dados do usuário cadastrado
     * @throws EmailAlreadyExistsException se o e-mail já estiver registrado
     */
    @Transactional
    public UserResponseDTO register(UserRequestDTO dto) {
        String email = normalizeEmail(dto.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }
        User user = new User();
        user.setUsername(dto.username().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setRole(Role.OPERATOR);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return toResponseDTO(savedUser);
    }

    /**
     * Retorna todos os usuários cadastrados.
     *
     * @return lista de usuários convertidos para DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca um usuário pelo identificador.
     *
     * @param id identificador UUID do usuário
     * @return dados do usuário encontrado
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        return toResponseDTO(user);
    }

    /**
     * Atualiza o nome de usuário e o e-mail de um usuário existente.
     *
     * @param id identificador UUID do usuário
     * @param dto novos dados do usuário
     * @return dados atualizados do usuário
     * @throws ResourceNotFoundException se o usuário não existir
     * @throws EmailAlreadyExistsException se o novo e-mail pertencer
     *         a outro usuário
     */
    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO dto) {
        User user = getUserOrThrow(id);
        String email = normalizeEmail(dto.email());

        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        user.setUsername(dto.username().trim());
        user.setEmail(email);

        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    /**
     * Exclui um usuário pelo identificador.
     *
     * @param id identificador UUID do usuário
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional
    public void delete(UUID id) {
        User user = getUserOrThrow(id);

        userRepository.delete(user);
    }

    /**
     * Atualiza o perfil de acesso de um usuário.
     *
     * @param id identificador UUID do usuário
     * @param dto dados contendo o novo perfil
     * @return dados atualizados do usuário
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional
    public UserResponseDTO updateRole(UUID id, UserRoleUpdateRequestDTO dto) {
        User user = getUserOrThrow(id);

        user.setRole(dto.role());

        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    /**
     * Converte uma entidade de usuário para seu DTO de resposta,
     * sem expor o hash da senha.
     *
     * @param user entidade do usuário
     * @return DTO com os dados públicos do usuário
     */
    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

    /**
     * Busca um usuário ou lança uma exceção caso não exista.
     *
     * @param id identificador UUID do usuário
     * @return entidade do usuário encontrado
     * @throws ResourceNotFoundException se o usuário não existir
     */
    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
