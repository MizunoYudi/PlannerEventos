package com.example.plannereventos.repository;

import com.example.plannereventos.model.Inscricao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InscricaoRepositoryTest {

    private static final int EVENTO_ID = 1;

    private InscricaoRepository repository;
    private UUID participanteId;

    @BeforeEach
    void setUp() {
        repository = new InscricaoRepository();
        participanteId = UUID.randomUUID();
    }

    private Inscricao salvar(String status) {
        return repository.salvar(new Inscricao(0, EVENTO_ID, participanteId, LocalDateTime.now(), status, null));
    }

    @Test
    @DisplayName("Deve devolver a unica inscricao do participante no evento")
    void deveBuscarUnicaInscricao() {
        Inscricao salva = salvar("CONFIRMADA");

        assertEquals(salva.getId(), repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).get().getId());
    }

    @Test
    @DisplayName("Deve preferir a inscricao confirmada quando o participante cancelou e se inscreveu de novo")
    void devePreferirInscricaoConfirmada() {
        salvar("CANCELADA");
        Inscricao confirmada = salvar("CONFIRMADA");

        Inscricao encontrada = repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).get();

        assertEquals(confirmada.getId(), encontrada.getId());
        assertEquals("CONFIRMADA", encontrada.getStatus());
    }

    @Test
    @DisplayName("Deve devolver a inscricao cancelada mais recente quando nao houver confirmada")
    void deveDevolverCanceladaMaisRecenteQuandoNaoHouverConfirmada() {
        salvar("CANCELADA");
        Inscricao maisRecente = salvar("CANCELADA");

        assertEquals(maisRecente.getId(), repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).get().getId());
    }

    @Test
    @DisplayName("Deve devolver vazio quando o participante nao tem inscricao no evento")
    void deveDevolverVazioSemInscricao() {
        assertTrue(repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).isEmpty());
        assertTrue(repository.buscarPorEventoEParticipante(EVENTO_ID, null).isEmpty());
    }

    @Test
    @DisplayName("Nao deve misturar inscricoes de outro evento")
    void naoDeveMisturarEventos() {
        salvar("CONFIRMADA");

        assertTrue(repository.buscarPorEventoEParticipante(2, participanteId).isEmpty());
    }
}