package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ParticipanteCreateRequest {

    @NotBlank(message = "O nome completo e obrigatorio")
    private String nomeCompleto;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "O e-mail deve possuir um formato valido")
    private String email;

    public ParticipanteCreateRequest() {
    }

    public ParticipanteCreateRequest(String nomeCompleto, String email) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}