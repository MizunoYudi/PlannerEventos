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
    public ResponseEntity<ParticipanteResponse> cadastrarParticipante(@Valid @RequestBody ParticipanteCreateRequest request) {
        ParticipanteResponse response = participanteService.cadastrarParticipante(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{participanteId}/inscricoes")
    public ResponseEntity<?> listarInscricoesDoParticipante(@PathVariable UUID participanteId) {
        try {
            List<Inscricao> lista = inscricaoService.listarPorParticipante(participanteId);
            return ResponseEntity.ok(lista); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipanteResponse> atualizarParticipante(
            @PathVariable UUID id,
            @Valid @RequestBody ParticipanteCreateRequest request) {
        ParticipanteResponse response = participanteService.atualizarParticipante(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteResponse>> listarParticipantes() {
        List<ParticipanteResponse> participantes = participanteService.listarParticipantes();
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteResponse> buscarPorId(@PathVariable UUID id) {
        ParticipanteResponse participante = participanteService.buscarPorId(id);
        return ResponseEntity.ok(participante);
    }
}