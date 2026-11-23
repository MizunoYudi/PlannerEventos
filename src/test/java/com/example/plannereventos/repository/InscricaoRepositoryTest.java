package com.example.plannereventos.repository;

import com.example.plannereventos.config.Fuso;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.model.LoteInscricao;
import com.example.plannereventos.model.StatusInscricao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscricaoRepositoryTest {

    private static final int EVENTO_ID = 1;

    private InscricaoRepository repository;
    private UUID participanteId;

    @BeforeEach
    void setUp() {
        repository = new InscricaoRepository();
        participanteId = UUID.randomUUID();
    }

    private Inscricao salvar(StatusInscricao status) {
        return repository.salvar(new Inscricao(
                0,
                EVENTO_ID,
                participanteId,
                LocalDateTime.now(Fuso.BRASILIA),
                status,
                LoteInscricao.LOTE_1,
                100.0,
                null
        ));
    }

    @Test
    @DisplayName("Deve devolver a unica inscricao do participante no evento")
    void deveBuscarUnicaInscricao() {
        Inscricao salva = salvar(StatusInscricao.CONFIRMADA);

        assertEquals(salva.getId(), repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).get().getId());
    }

    @Test
    @DisplayName("Deve preferir a inscricao confirmada quando o participante cancelou e se inscreveu de novo")
    void devePreferirInscricaoConfirmada() {
        salvar(StatusInscricao.CANCELADA);
        Inscricao confirmada = salvar(StatusInscricao.CONFIRMADA);

        Inscricao encontrada = repository.buscarPorEventoEParticipante(EVENTO_ID, participanteId).get();

        assertEquals(confirmada.getId(), encontrada.getId());
        assertEquals(StatusInscricao.CONFIRMADA, encontrada.getStatus());
    }

    @Test
    @DisplayName("Deve devolver a inscricao cancelada mais recente quando nao houver confirmada")
    void deveDevolverCanceladaMaisRecenteQuandoNaoHouverConfirmada() {
        salvar(StatusInscricao.CANCELADA);
        Inscricao maisRecente = salvar(StatusInscricao.CANCELADA);

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
        salvar(StatusInscricao.CONFIRMADA);

        assertTrue(repository.buscarPorEventoEParticipante(2, participanteId).isEmpty());
    }

    @Test
    @DisplayName("RN17 - Cancelar inscrição confirmada deve liberar a vaga")
    void cancelarInscricaoConfirmadaDeveLiberarVaga() {
        InscricaoRepository repository = new InscricaoRepository();
        Inscricao inscricao = new Inscricao(0, 1, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_1, 90.0, null);
        repository.salvar(inscricao);

        inscricao.confirmar();
        repository.salvar(inscricao);
        assertEquals(1, repository.contarConfirmadasPorEvento(1));

        inscricao.cancelar("Desistência");
        repository.salvar(inscricao);
        assertEquals(0, repository.contarConfirmadasPorEvento(1));
    }

    @Test
    @DisplayName("Deve cancelar as inscricoes ativas do evento e manter o motivo das ja canceladas")
    void deveCancelarInscricoesAtivasDoEvento() {
        InscricaoRepository repository = new InscricaoRepository();

        Inscricao pendente = new Inscricao(0, 1, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_2, 0.0, null);
        Inscricao confirmada = new Inscricao(0, 1, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.CONFIRMADA, LoteInscricao.LOTE_2, 0.0, null);
        Inscricao jaCancelada = new Inscricao(0, 1, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.CANCELADA, LoteInscricao.LOTE_2, 0.0, "Motivo original");
        Inscricao outroEvento = new Inscricao(0, 2, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.CONFIRMADA, LoteInscricao.LOTE_2, 0.0, null);

        repository.salvar(pendente);
        repository.salvar(confirmada);
        repository.salvar(jaCancelada);
        repository.salvar(outroEvento);

        List<Inscricao> canceladas = repository.cancelarTodasPorEvento(1, "Evento cancelado");

        assertEquals(2, canceladas.size());
        assertEquals(StatusInscricao.CANCELADA, pendente.getStatus());
        assertEquals(StatusInscricao.CANCELADA, confirmada.getStatus());
        assertEquals("Motivo original", jaCancelada.getMotivoCancelamento());
        assertEquals(StatusInscricao.CONFIRMADA, outroEvento.getStatus());
    }

    @Test
    @DisplayName("listarPendentes deve devolver apenas inscricoes PENDENTE")
    void deveListarApenasPendentes() {
        Inscricao pendente = salvar(StatusInscricao.PENDENTE);
        salvar(StatusInscricao.CONFIRMADA);
        salvar(StatusInscricao.CANCELADA);

        List<Inscricao> pendentes = repository.listarPendentes();

        assertEquals(1, pendentes.size());
        assertEquals(pendente.getId(), pendentes.get(0).getId());
    }

    @Test
    @DisplayName("listarPendentes deve devolver lista vazia quando nao houver pendentes")
    void deveDevolverListaVaziaSemPendentes() {
        salvar(StatusInscricao.CONFIRMADA);
        salvar(StatusInscricao.CANCELADA);

        assertTrue(repository.listarPendentes().isEmpty());
    }

    @Test
    @DisplayName("listarPendentes deve incluir pendentes de todos os eventos")
    void deveListarPendentesDeVariosEventos() {
        salvar(StatusInscricao.PENDENTE);
        repository.salvar(new Inscricao(0, 2, UUID.randomUUID(), LocalDateTime.now(Fuso.BRASILIA),
                StatusInscricao.PENDENTE, LoteInscricao.LOTE_1, 100.0, null));

        assertEquals(2, repository.listarPendentes().size());
    }
}