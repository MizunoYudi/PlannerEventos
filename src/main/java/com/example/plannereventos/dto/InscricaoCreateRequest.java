package dto;

public class InscricaoCreateRequest {
    private Long participanteId;

    public InscricaoCreateRequest() {
    }

    public InscricaoCreateRequest(Long participanteId) {
        this.participanteId = participanteId;
    }

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }
}