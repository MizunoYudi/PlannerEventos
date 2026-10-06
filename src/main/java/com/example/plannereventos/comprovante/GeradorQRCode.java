package com.example.plannereventos.comprovante;

import com.example.plannereventos.model.Inscricao;

public interface GeradorQRCode {

    String gerarHash(Inscricao inscricao);
}
