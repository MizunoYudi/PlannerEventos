package com.example.plannereventos.comprovante;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.plannereventos.model.Comprovante;
import com.example.plannereventos.model.Inscricao;

class ComprovanteDigitalServiceTest {

    private final ComprovanteDigitalService service
            = new ComprovanteDigitalService();

    @Test
    @DisplayName("Deve gerar comprovante digital com hash")
    void deveGerarComprovanteDigitalComHash() {

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
                "DIGITAL",
                comprovante.getTipo()
        );

        assertNotNull(comprovante.getHash());

        assertFalse(
                comprovante.getHash().isBlank()
        );
    }

    @Test
    @DisplayName("Hash SHA-256 deve possuir 64 caracteres")
    void hashDevePossuirFormatoSha256() {

        Inscricao inscricao = new Inscricao(
                1,
                10,
                UUID.randomUUID(),
                LocalDateTime.now(),
                "CONFIRMADA",
                null
        );

        String hash = service.gerarHash(inscricao);

        assertEquals(64, hash.length());

        assertTrue(
                hash.matches("[a-f0-9]{64}")
        );
    }

    @Test
    @DisplayName("Participantes diferentes devem gerar hashes diferentes")
    void participantesDiferentesDevemGerarHashesDiferentes() {

        LocalDateTime momento
                = LocalDateTime.of(
                        2026,
                        10,
                        4,
                        20,
                        0
                );

        Inscricao primeira = new Inscricao(
                1,
                10,
                UUID.randomUUID(),
                momento,
                "CONFIRMADA",
                null
        );

        Inscricao segunda = new Inscricao(
                2,
                10,
                UUID.randomUUID(),
                momento,
                "CONFIRMADA",
                null
        );

        String hash1
                = service.gerarHash(primeira);

        String hash2
                = service.gerarHash(segunda);

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Eventos diferentes devem gerar hashes diferentes")
    void eventosDiferentesDevemGerarHashesDiferentes() {

        UUID participanteId = UUID.randomUUID();

        LocalDateTime momento
                = LocalDateTime.of(
                        2026,
                        10,
                        4,
                        20,
                        0
                );

        Inscricao primeira = new Inscricao(
                1,
                10,
                participanteId,
                momento,
                "CONFIRMADA",
                null
        );

        Inscricao segunda = new Inscricao(
                2,
                20,
                participanteId,
                momento,
                "CONFIRMADA",
                null
        );

        assertNotEquals(
                service.gerarHash(primeira),
                service.gerarHash(segunda)
        );
    }

    @Test
    @DisplayName("Timestamps diferentes devem gerar hashes diferentes")
    void timestampsDiferentesDevemGerarHashesDiferentes() {

        UUID participanteId = UUID.randomUUID();

        Inscricao primeira = new Inscricao(
                1,
                10,
                participanteId,
                LocalDateTime.of(
                        2026, 10, 4, 20, 0
                ),
                "CONFIRMADA",
                null
        );

        Inscricao segunda = new Inscricao(
                2,
                10,
                participanteId,
                LocalDateTime.of(
                        2026, 10, 4, 20, 1
                ),
                "CONFIRMADA",
                null
        );

        assertNotEquals(
                service.gerarHash(primeira),
                service.gerarHash(segunda)
        );
    }

    @Test
    @DisplayName("Deve gerar hash usando evento participante e timestamp")
    void deveGerarHashComPayloadCorreto() throws Exception {

        UUID participanteId = UUID.fromString(
                "123e4567-e89b-12d3-a456-426614174000"
        );

        LocalDateTime timestamp = LocalDateTime.of(
                2026,
                10,
                5,
                10,
                30
        );

        Inscricao inscricao = new Inscricao(
                1,
                10,
                participanteId,
                timestamp,
                "CONFIRMADA",
                null
        );

        String payload
                = "10:"
                + participanteId
                + ":"
                + timestamp;

        MessageDigest digest
                = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = digest.digest(
                payload.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hashEsperado
                = new StringBuilder();

        for (byte b : hashBytes) {
            hashEsperado.append(
                    String.format("%02x", b)
            );
        }

        assertEquals(
                hashEsperado.toString(),
                service.gerarHash(inscricao)
        );
    }

}
