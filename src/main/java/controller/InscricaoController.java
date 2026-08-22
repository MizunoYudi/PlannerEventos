package br.ifsp.boituva.gestaoeventos.controller;

import br.ifsp.boituva.gestaoeventos.model.Inscricao;
import br.ifsp.boituva.gestaoeventos.service.InscricaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final InscricaoService inscricaoService;

    public EventoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    // GET /api/eventos/{eventoId}/inscricoes
    @GetMapping("/{eventoId}/inscricoes")
    public ResponseEntity<?> listarInscricoesDoEvento(@PathVariable Long eventoId) {
        try {
            List<Inscricao> lista = inscricaoService.listarPorEvento(eventoId);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // GET /api/eventos/{eventoId}/inscricoes/{participanteId} (RF15)
    @GetMapping("/{eventoId}/inscricoes/{participanteId}")
    public ResponseEntity<?> buscarInscricaoEspecifica(
            @PathVariable Long eventoId,
            @PathVariable Long participanteId) {
        try {
            Inscricao inscricao = inscricaoService.buscarPorEventoEParticipante(eventoId, participanteId);
            return ResponseEntity.ok(inscricao); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }
}