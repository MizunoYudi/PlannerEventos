package com.example.plannereventos.strategy;

import com.example.plannereventos.exception.MotivoCancelamentoObrigatorioException;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import org.springframework.stereotype.Component;

@Component
public class CancelamentoEventoRestricaoIdadeStrategy
        implements PoliticaCancelamentoStrategy {

    @Override
    public void validarCancelamento(
            Inscricao inscricao,
            String motivo) {

        if (motivo == null || motivo.isBlank()) {
            throw new MotivoCancelamentoObrigatorioException();
        }
    }

    @Override
    public ModalidadeEvento getModalidade() {
        return ModalidadeEvento.RESTRICAO_IDADE;
    }
}