package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;
import org.springframework.stereotype.Component;

@Component("calculoValorEventoAbertoStrategy")
public class CalculoValorEventoAbertoStrategy implements CalculoValorFinalStrategy {

    @Override
    public double calcular(double valorBase, LoteInscricao lote) {
        return 0.0;
    }
}