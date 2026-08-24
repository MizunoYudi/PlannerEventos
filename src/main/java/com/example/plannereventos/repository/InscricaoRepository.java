package com.example.plannereventos.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.plannereventos.model.Inscricao;
import org.springframework.stereotype.Repository;

@Repository

public class InscricaoRepository {
    private List<Inscricao> inscricoes = new ArrayList<>();
    private int proximoId = 1;

    //Cadastrar
    public Inscricao cadastrar(Inscricao inscricao) {
        inscricao.setId(proximoId);
        proximoId++;

        inscricoes.add(inscricao);
        return inscricao;
    }

    //Listar
    public List<Inscricao> listar() {
        return inscricoes;
    }

    //Buscar
    public Inscricao buscarPorId(int id) {
        for (Inscricao p : inscricoes) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
    //cancelar
    public boolean cancelar(int id) {
        Inscricao inscricao = buscarPorId(id);
        if (inscricao != null) {
            inscricao.setStatus("CANCELADA");
            return true;
        }
        return false;
    }

    //Listar inscricoes do evento
    public List<Inscricao> listarPorEvento(int idEvento) {
        List<Inscricao> filtradas = new ArrayList<>();
        for (Inscricao p : inscricoes) {
            if (p.getIdEvento() == idEvento) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    //Listar inscricoes do participante
    public List<Inscricao> listarPorParticipante(UUID participanteId) {
        List<Inscricao> filtradas = new ArrayList<>();
        for (Inscricao p : inscricoes) {
            if (p.getParticipanteId().equals(participanteId)) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    //Contar vagas ocupadas
    public int contarConfirmadasPorEvento(int idEvento) {
        int contador = 0;
        for (Inscricao p : inscricoes) {
            if (p.getIdEvento() == idEvento && "CONFIRMADA".equalsIgnoreCase(p.getStatus())) {
                contador++;
            }
        }
        return contador;
    }

}

