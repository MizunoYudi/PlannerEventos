package com.example.plannereventos.strategy;

import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;

public interface PoliticaCancelamentoStrategy {

    void validarCancelamento(Inscricao inscricao, String motivo);

    ModalidadeEvento getModalidade();
}