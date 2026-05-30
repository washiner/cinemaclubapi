package com.washiner.cinemaclubapi.exception;

public class UsuarioNaoEncontradoException extends RuntimeException{

    public UsuarioNaoEncontradoException(String email){

        super("Usuário não encontrado: " + email);

    }
}
