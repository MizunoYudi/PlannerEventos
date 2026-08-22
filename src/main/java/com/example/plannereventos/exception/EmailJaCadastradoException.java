package com.example.plannereventos.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Ja existe um participante cadastrado com o e-mail: " + email);
    }
}
