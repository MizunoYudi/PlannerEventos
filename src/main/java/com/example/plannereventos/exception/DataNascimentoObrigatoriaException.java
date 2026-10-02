package com.example.plannereventos.exception;

public class DataNascimentoObrigatoriaException extends RuntimeException {
    public DataNascimentoObrigatoriaException() {
        super("A data de nascimento do participante é obrigatória para este evento.");
    }
}