package com.washiner.cinemaclubapi.exception;

// Extends RuntimeException → não precisa declarar no método com throws
// Runtime = erro em tempo de execução — não precisa ser tratado obrigatoriamente
public class EmailJaCadastradoException extends RuntimeException{

    // Construtor recebe o email para montar mensagem clara
    // "Email já cadastrado: washiner@email.com"
    public EmailJaCadastradoException(String email){
        super("Email já Cadastrado" + email);
    }
}


//🔑 Raciocínio — por que extends RuntimeException?
//Exception         → obriga try-catch em todo lugar — verboso
//RuntimeException  → opcional o try-catch — mais limpo
//Todas as nossas exceptions de negócio estendem RuntimeException