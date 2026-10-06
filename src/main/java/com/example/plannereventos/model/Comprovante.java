package com.example.plannereventos.model;

public class Comprovante {

    private final String tipo;
    private final String resumo;
    private final String hash;

    public Comprovante(
            String tipo,
            String resumo,
            String hash) {

        this.tipo = tipo;
        this.resumo = resumo;
        this.hash = hash;
    }

    public String getTipo() {
        return tipo;
    }

    public String getResumo() {
        return resumo;
    }

    public String getHash() {
        return hash;
    }
}
