package com.example.plannereventos.controller;

import com.example.plannereventos.dto.*;
import com.example.plannereventos.service.EventoService;

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
    public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoCreateRequest request) {
        EventoResponse response = eventoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizar(@PathVariable int id, @Valid @RequestBody EventoUpdateRequest request) {
        EventoResponse response = eventoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelamento")
    public ResponseEntity<EventoResponse> cancelar(@PathVariable int id) {
        EventoResponse response = eventoService.cancelar(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listar() {
        return ResponseEntity.ok(eventoService.listar());
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