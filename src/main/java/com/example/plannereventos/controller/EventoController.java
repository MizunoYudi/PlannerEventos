package com.example.plannereventos.controller;

import com.example.plannereventos.dto.*;
import com.example.plannereventos.model.Inscricao;
import com.example.plannereventos.service.EventoService;
import com.example.plannereventos.service.InscricaoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> salvarEvento(@Valid @RequestBody EventoCreateRequest request) {
        EventoResponse response = eventoService.salvarEvento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizarEvento(@PathVariable int id, @Valid @RequestBody EventoUpdateRequest request) {
        EventoResponse response = eventoService.atualizarEvento(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelamento")
    public ResponseEntity<EventoResponse> cancelarEvento(@PathVariable int id) {
        EventoResponse response = eventoService.cancelarEvento(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @GetMapping("/{id}/vagas")
    public ResponseEntity<EventoVagasResponse> consultarVagas(@PathVariable int id) {
        return ResponseEntity.ok(eventoService.consultarVagas(id));
    }
}