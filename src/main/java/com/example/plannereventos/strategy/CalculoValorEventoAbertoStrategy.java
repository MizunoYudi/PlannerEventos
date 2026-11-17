package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.ModalidadeEvento;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("calculoValorEventoAbertoStrategy")
public class CalculoValorEventoAbertoStrategy implements CalculoValorFinalStrategy {

    @Override
    public double calcular(double valorBase, LoteInscricao lote) {
        return 0.0;
    }
    @Override
    public Set<ModalidadeEvento> getModalidades() {
        return Set.of(ModalidadeEvento.ABERTO);
    }
}