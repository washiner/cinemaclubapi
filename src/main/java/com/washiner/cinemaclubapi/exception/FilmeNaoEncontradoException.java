package com.washiner.cinemaclubapi.exception;

public class FilmeNaoEncontradoException extends RuntimeException{

    public FilmeNaoEncontradoException(Long id){
        super("Filme não encontrado com id: " + id);
    }
}
