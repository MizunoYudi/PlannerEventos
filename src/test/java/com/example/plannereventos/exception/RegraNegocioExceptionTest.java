package com.example.plannereventos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RegraNegocioExceptionTest {

    @Test
    @DisplayName("Deve instanciar RegraNegocioException preservando a mensagem")
    void deveInstanciarComMensagem() {
        RegraNegocioException ex = new RegraNegocioException("Regra de negócio violada");

        assertInstanceOf(RuntimeException.class, ex);
        assertEquals("Regra de negócio violada", ex.getMessage());
    }
}