package com.example.plannereventos.controller;

import com.example.plannereventos.dto.EventoCreateRequest;
import com.example.plannereventos.dto.EventoResponse;
import com.example.plannereventos.dto.InscricaoCreateRequest;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.service.EventoService;
import com.example.plannereventos.service.InscricaoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final InscricaoService inscricaoService;

    public EventoController(EventoService eventoService, InscricaoService inscricaoService) {
        this.eventoService = eventoService;
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoCreateRequest request) {
        EventoResponse response = eventoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/eventos/{eventoId}/inscricoes
    @PostMapping("/{eventoId}/inscricoes")
    public ResponseEntity<?> realizarInscricao(
            @PathVariable Long eventoId,
            @RequestBody InscricaoCreateRequest request) {
        @RequestBody InscricaoCreateRequest request){
            try {
                Inscricao inscricaoCriada = inscricaoService.inscrever(eventoId, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(inscricaoCriada); // 201
            } catch (RuntimeException e) {
                if (e.getMessage().contains("não encontrado")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
            }
        }
    }