package com.example.plannereventos.strategy;

import com.example.plannereventos.model.LoteInscricao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoValorFinalStrategyTest {

    private final CalculoValorPadraoStrategy calculoPadrao = new CalculoValorPadraoStrategy();
    private final CalculoValorEventoAbertoStrategy calculoEventoAberto = new CalculoValorEventoAbertoStrategy();

    @Test
    @DisplayName("RF22 / RN08 - Deve aplicar 20% de desconto no Lote Promocional para evento padrão")
    void deveAplicarDescontoLotePromocional() {
        double valorFinal = calculoPadrao.calcular(100.0, LoteInscricao.PROMOCIONAL);
        assertEquals(80.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("RF22 / RN08 - Deve aplicar 10% de desconto no Lote 1 para evento padrão")
    void deveAplicarDescontoLote1() {
        double valorFinal = calculoPadrao.calcular(100.0, LoteInscricao.LOTE_1);
        assertEquals(90.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("RF22 / RN08 - Deve aplicar 0% de desconto no Lote 2 para evento padrão")
    void deveAplicarDescontoLote2() {
        double valorFinal = calculoPadrao.calcular(100.0, LoteInscricao.LOTE_2);
        assertEquals(100.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("RN08 - Evento da modalidade Aberto deve retornar sempre zero independentemente do lote")
    void eventoAbertoDeveTerValorZeroIndependentementeDoLote() {
        assertEquals(0.0, calculoEventoAberto.calcular(150.0, LoteInscricao.PROMOCIONAL), 0.001);
        assertEquals(0.0, calculoEventoAberto.calcular(150.0, LoteInscricao.LOTE_1), 0.001);
        assertEquals(0.0, calculoEventoAberto.calcular(150.0, LoteInscricao.LOTE_2), 0.001);
    }
}