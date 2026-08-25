package com.example.plannereventos.controller;

import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.dto.InscricaoResponse;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.service.InscricaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/inscricoes")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    public ResponseEntity<Inscricao> inscrever(
            @PathVariable int eventoId,
            @Valid @RequestBody InscricaoCreateRequest request) {
        Inscricao inscricao = inscricaoService.inscrever(eventoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inscricao);
    }

    // Listar inscrições do evento
    @GetMapping
    public ResponseEntity<List<Inscricao>> listarPorEvento(@PathVariable int eventoId) {
        return ResponseEntity.ok(inscricaoService.listarPorEvento(eventoId));
    }

    @GetMapping("/{participanteId}")
    public ResponseEntity<InscricaoResponse> buscarInscricao(
            @PathVariable int eventoId,
            @PathVariable UUID participanteId) {
        return ResponseEntity.ok(inscricaoService.buscarPorEventoEParticipante(eventoId, participanteId));
    }

    @DeleteMapping("/{participanteId}")
    public ResponseEntity<Void> cancelarInscricao(
            @PathVariable int eventoId,
            @PathVariable UUID participanteId) {
        inscricaoService.cancelarInscricao(eventoId, participanteId);
        return ResponseEntity.noContent().build();
    }
}