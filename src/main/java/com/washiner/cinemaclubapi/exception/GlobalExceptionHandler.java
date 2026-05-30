package com.washiner.cinemaclubapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice → intercepta exceptions de todos os controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erros de validação — @NotBlank, @Email, @Size...
    // Devolve 422 com lista de campos e erros
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Erro de validação");
        problem.setProperty("erros", erros);
        return problem;
    }

    // Email duplicado → 409 Conflict
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Usuário não encontrado → 404 Not Found
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ProblemDetail handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Filme não encontrado → 404 Not Found
    @ExceptionHandler(FilmeNaoEncontradoException.class)
    public ProblemDetail handleFilmeNaoEncontrado(FilmeNaoEncontradoException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Refresh token inválido → 401 Unauthorized
    @ExceptionHandler(RefreshTokenInvalidoException.class)
    public ProblemDetail handleRefreshTokenInvalido(RefreshTokenInvalidoException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // Qualquer outro erro → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno — contate o administrador");
    }
}