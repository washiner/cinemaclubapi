package com.washiner.cinemaclubapi.exception;

public class RefreshTokenInvalidoException extends RuntimeException{

    public RefreshTokenInvalidoException(){
        super("Refresh token inválido ou expirado");
    }
}
