package com.example.plannereventos.controller;

import com.example.plannereventos.dto.ParticipanteCreateRequest;
import com.example.plannereventos.dto.ParticipanteResponse;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.service.InscricaoService;
import com.example.plannereventos.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final InscricaoService inscricaoService;

    public ParticipanteController(ParticipanteService participanteService, InscricaoService inscricaoService) {
        this.participanteService = participanteService;
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> cadastrar(@Valid @RequestBody ParticipanteCreateRequest request) {
        ParticipanteResponse response = participanteService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{participanteId}/inscricoes")
    public ResponseEntity<?> listarInscricoesDoParticipante(@PathVariable Long participanteId) {
        try {
            List<Inscricao> lista = inscricaoService.listarPorParticipante(participanteId);
            return ResponseEntity.ok(lista); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipanteResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ParticipanteCreateRequest request) {
        ParticipanteResponse response = participanteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
}