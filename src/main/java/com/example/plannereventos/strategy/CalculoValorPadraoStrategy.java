package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("calculoValorPadraoStrategy")
public class CalculoValorPadraoStrategy implements CalculoValorFinalStrategy {

    @Override
    public double calcular(double valorBase, LoteInscricao lote) {
        if (lote == null) {
            return valorBase;
        }

        double desconto = lote.getPercentualDesconto();
        return valorBase * (1.0 - desconto);
    }
    @Override
    public Set<ModalidadeEvento> getModalidades() {
        return Set.of(ModalidadeEvento.EXCLUSIVO_ALUNOS, ModalidadeEvento.RESTRICAO_IDADE);
    }
}