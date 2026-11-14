package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;

public interface CalculoValorFinalStrategy {
    /**
     * Calcula o valor final da inscrição com base no valor base do evento e no lote ativo.
     *
     * @param valorBase Valor base cadastrado no evento
     * @param lote Lote ativo da inscrição (ex: PROMOCIONAL, LOTE_1, LOTE_2)
     * @return Valor final calculado com o desconto aplicado
     */
    double calcular(double valorBase, LoteInscricao lote);
}