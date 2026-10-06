package com.example.plannereventos.comprovante;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.plannereventos.model.Comprovante;
import com.example.plannereventos.model.Inscricao;

class ComprovanteSimplesServiceTest {

    private final ComprovanteSimplesService service
            = new ComprovanteSimplesService();

    @Test
    @DisplayName("Deve gerar comprovante simples sem hash")
    void deveGerarComprovanteSimplesSemHash() {

        Inscricao inscricao = new Inscricao(
                1,
                10,
                UUID.randomUUID(),
                LocalDateTime.now(),
                "CONFIRMADA",
                null
        );

        Comprovante comprovante
                = service.emitir(inscricao);

        assertNotNull(comprovante);

        assertEquals(
                "SIMPLES",
                comprovante.getTipo()
        );

        assertNotNull(comprovante.getResumo());

        assertNull(comprovante.getHash());
    }

    @Test
    @DisplayName("Comprovante simples nao deve implementar geracao de QR Code")
    void comprovanteSimplesNaoDeveImplementarQrCode() {

        assertFalse(
                GeradorQRCode.class.isAssignableFrom(
                        ComprovanteSimplesService.class
                )
        );
    }
}
