import java.util.ArrayList;
import java.util.List;

import br.ifsp.boituva.gestaoeventos.model.Inscricao;
import org.springframework.stereotype.Repository;

@Repository

public class InscricaoRepository {
    private List<Inscricao> inscricoes = new ArrayList<>();
    private Long proximoId = 1L; //L porque é long

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
    public Inscricao buscarPorId(Long id) {
        for (Inscricao p : inscricoes) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
//cancelar
    public boolean cancelar(Long id) {
        Inscricao inscricao = buscarPorId(id);
        if (inscricao != null) {
            inscricao.setStatus("CANCELADA");
            return true;
        }
        return false;
    }

    //Listar inscricoes do evento
    public List<Inscricao> listarPorEvento(Long idEvento) {
        List<Inscricao> filtradas = new ArrayList<>();
        for (Inscricao p : inscricoes) {
            if (p.getIdEvento().equals(idEvento)) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    //Listar inscricoes do participante
    public List<Inscricao> listarPorParticipante(Long participanteId) {
        List<Inscricao> filtradas = new ArrayList<>();
        for (Inscricao p : inscricoes) {
            if (p.getParticipanteId().equals(participanteId)) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    //Contar vagas ocupadas
    public int contarConfirmadasPorEvento(Long idEvento) {
        int contador = 0;
        for (Inscricao p : inscricoes) {
            if (p.getIdEvento().equals(idEvento) && "CONFIRMADA".equalsIgnoreCase(p.getStatus())) {
                contador++;
            }
        }
        return contador;
    }
}

