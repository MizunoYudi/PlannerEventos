package controller;

import dto.ParticipanteCreateRequest;
import dto.ParticipanteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ParticipanteService;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> cadastrar(@Valid @RequestBody ParticipanteCreateRequest request) {
        ParticipanteResponse response = participanteService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}