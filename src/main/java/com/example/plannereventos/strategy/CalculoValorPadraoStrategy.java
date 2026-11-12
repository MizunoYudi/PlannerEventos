package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;
import org.springframework.stereotype.Component;

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
}