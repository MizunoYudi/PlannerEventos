package com.example.plannereventos.exception;

public class ParticipanteSemMatriculaAtivaException extends RuntimeException {
    public ParticipanteSemMatriculaAtivaException() {
        super("Inscrição permitida apenas para alunos com matrícula ativa.");
    }
}