package com.example.plannereventos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeHashServiceTest {

    private final QrCodeHashService hashService = new QrCodeHashService();

    private final UUID participanteId = UUID.randomUUID();
    private final LocalDateTime dataInscricao = LocalDateTime.of(2026, 10, 5, 9, 30);

    @Test
    @DisplayName("Deve gerar hash SHA-256 com 64 caracteres hexadecimais")
    void deveGerarHashSha256EmHexadecimal() {
        String hash = hashService.gerarHash(1, participanteId, dataInscricao);

        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Mesmos dados devem gerar sempre o mesmo hash")
    void mesmosDadosDevemGerarMesmoHash() {
        String primeiro = hashService.gerarHash(1, participanteId, dataInscricao);
        String segundo = hashService.gerarHash(1, participanteId, dataInscricao);

        assertEquals(primeiro, segundo);
    }

    @Test
    @DisplayName("Hash deve mudar quando o evento mudar")
    void hashDeveMudarQuandoEventoMudar() {
        assertNotEquals(
                hashService.gerarHash(1, participanteId, dataInscricao),
                hashService.gerarHash(2, participanteId, dataInscricao));
    }

    @Test
    @DisplayName("Hash deve mudar quando o participante mudar")
    void hashDeveMudarQuandoParticipanteMudar() {
        assertNotEquals(
                hashService.gerarHash(1, participanteId, dataInscricao),
                hashService.gerarHash(1, UUID.randomUUID(), dataInscricao));
    }

    @Test
    @DisplayName("Hash deve mudar quando o timestamp da inscricao mudar")
    void hashDeveMudarQuandoTimestampMudar() {
        assertNotEquals(
                hashService.gerarHash(1, participanteId, dataInscricao),
                hashService.gerarHash(1, participanteId, dataInscricao.plusSeconds(1)));
    }
}