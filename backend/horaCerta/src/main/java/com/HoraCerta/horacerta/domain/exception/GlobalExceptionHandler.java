package com.HoraCerta.horacerta.domain.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centraliza o tratamento de exceções da API e padroniza
 * as respostas HTTP retornadas em situações de erro.
 *
 * @author Maria Luiza
 */
@RestControllerAdvice
public class GlobalExceptionHandler{

    /**
     * Representa a estrutura padrão das respostas de erro da API.
     *
     * @param timestamp data e hora em que o erro foi registrado
     * @param Status código HTTP da resposta
     * @param error descrição textual do status HTTP
     * @param message mensagem explicativa do erro
     * @param path caminho da requisição que originou o erro
     * @param validationErros erros de validação associados aos campos
     */
    public record ErrorResponse(
            LocalDateTime timestamp,
            int Status,
            String error,
            String message,
            String path,
            Map<String, String> validationErros
    ) {}

    /**
     * Monta uma resposta de erro com informações adicionais,
     * incluindo os erros de validação, quando existentes.
     *
     * @param status status HTTP da resposta
     * @param message mensagem explicativa
     * @param request requisição que originou o erro
     * @param validationErros erros associados aos campos
     * @return resposta HTTP padronizada
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErros
    ){
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErros
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Monta uma resposta de erro sem erros específicos de validação.
     *
     * @param status status HTTP da resposta
     * @param message mensagem explicativa
     * @param request requisição que originou o erro
     * @return resposta HTTP padronizada
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ){
        return buildResponse(status, message, request, Map.of());
    }

    /**
     * Trata conflitos com dados existentes ou violações
     * de integridade dos dados.
     *
     * @param ex exceção que originou o conflito
     * @param request requisição que originou o erro
     * @return resposta HTTP 409 Conflict
     */
    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            DuplicateEquipmentCodeException.class,
            EquipmentPlanAlreadyActiveException.class,
            EquipmentHasHistoryException.class,
            MaintenancePlanHasHistoryException.class,
            MaintenancePlanItemHasHistoryException.class,
            DataIntegrityViolationException.class
    })

    public ResponseEntity<ErrorResponse> handleConflict(
            Exception ex,
            HttpServletRequest request
    ){
        String message = ex instanceof DataIntegrityViolationException
                ? "The operation conflicts with existing data."
                : ex.getMessage();

        return buildResponse(HttpStatus.CONFLICT, message, request);
    }

    /**
     * Trata tentativas de operação que envolvam um plano de manutenção inativo.
     *
     * @param ex exceção que representa o plano inativo
     * @param request requisição que originou o erro
     * @return resposta HTTP 409 Conflict
     */
    @ExceptionHandler(InactiveMaintenancePlanException.class)
    public ResponseEntity<ErrorResponse> handleInactiveMaintenancePlan(
            InactiveMaintenancePlanException ex,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.CONFLICT,ex.getMessage(),
                request
        );
    }

    /**
     * Trata falhas de validação dos dados recebidos na requisição.
     *
     * @param ex exceção que contém os erros de validação
     * @param request requisição que originou o erro
     * @return resposta HTTP 400 com os campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        return buildResponse(HttpStatus.BAD_REQUEST,
                "One or more fields are invalid",
                request,
                errors
        );
    }

    /**
     * Trata corpos de requisição inválidos ou valores que não podem ser lidos.
     *
     * @param ex exceção de leitura da requisição
     * @param request requisição que originou o erro
     * @return resposta HTTP 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST,
                "The request body is invalid or contains an invalid value",
                request
        );
    }

    /**
     * Trata argumentos inválidos identificados durante o processamento.
     *
     * @param ex exceção que descreve o argumento inválido
     * @param request requisição que originou o erro
     * @return resposta HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request
        );
    }

    /**
     * Trata operações incompatíveis com o estado atual dos recursos.
     *
     * @param ex exceção que descreve o estado inválido
     * @param request requisição que originou o erro
     * @return resposta HTTP 409 Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT, ex.getMessage(), request
        );
    }

    /**
     * Trata tentativas de acesso a recursos sem permissão suficiente.
     *
     * @param ex exceção de acesso negado
     * @param request requisição que originou o erro
     * @return resposta HTTP 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action",
                request);
    }

    /**
     * Trata exceções não contempladas pelos handlers específicos.
     *
     * @param ex exceção inesperada
     * @param request requisição que originou o erro
     * @return resposta HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred.",
                request
        );
    }

    /**
     * Trata falhas de autenticação causadas por credenciais inválidas.
     *
     * @param ex exceção de credenciais inválidas
     * @param request requisição que originou o erro
     * @return resposta HTTP 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request
        );
    }

    /**
     * Trata tentativas de acesso a recursos inexistentes.
     *
     * @param ex exceção que identifica o recurso não encontrado
     * @param request requisição que originou o erro
     * @return resposta HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
