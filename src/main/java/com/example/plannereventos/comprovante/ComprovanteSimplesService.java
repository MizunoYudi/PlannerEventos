package com.example.plannereventos.comprovante;

import org.springframework.stereotype.Service;

import com.example.plannereventos.model.Comprovante;
import com.example.plannereventos.model.Inscricao;

@Service
public class ComprovanteSimplesService
        implements EmissorComprovante {

    @Override
    public Comprovante emitir(Inscricao inscricao) {
        String resumo
                = "Inscricao confirmada - Evento: "
                + inscricao.getIdEvento()
                + " - Participante: "
                + inscricao.getParticipanteId();
        return new Comprovante(
                "SIMPLES",
                resumo,
                null
        );
    }
}
