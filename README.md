# PlannerEventos

Sistema de Gestão de Eventos e Inscrições — uma API REST para cadastro e organização
de eventos, gerenciamento de participantes e controle de inscrições, com controle de
capacidade, prazos e status de eventos.

Esse projeto está sendo desenvolvido como um trabalho da matéria Qualidade de Software,
ministrada pelo Prof. Dr. Anisio Silva, no 6º período do Curso Tecnologia em Análise e Desenvolvimento
de Sistemas, no IFSP Campus Boituva.

## Integrantes

- Bruna Serra Amorim
- Helicássia Jesus da Silva
- João Victor Yudi Mizuno
- Victoria Benfica de Oliveira

## Pré-requisitos

- Java 21
- Maven (o projeto já inclui o Maven Wrapper, então não é obrigatório ter o Maven instalado)

## Como executar

Na raiz do projeto, rode:

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

A aplicação sobe por padrão na porta `8080`.

## Base da API

Todos os endpoints são servidos sob o prefixo:

/api

Exemplo local: `http://localhost:8080/api/eventos`

## Modalidades de evento

Todo evento possui uma modalidade, informada no campo `modalidade` do cadastro
(quando omitida, o evento é cadastrado como `ABERTO`).

| Modalidade | Elegibilidade para se inscrever | Cancelamento da inscrição | Comprovante |
|---|---|---|---|
| `ABERTO` | Existência de vagas disponíveis | Livre, até o início do evento | Simples |
| `EXCLUSIVO_ALUNOS` | Participante com número de matrícula informado e ativo | Até 24 horas antes do início do evento | Digital completo (com QR Code) |
| `RESTRICAO_IDADE` | Idade na data do evento maior ou igual à `idadeMinima` (exige data de nascimento no cadastro do participante) | Exige o motivo do cancelamento | Digital completo (com QR Code) |

## Endpoints

### Eventos
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/eventos` | Cadastrar evento |
| GET | `/api/eventos` | Listar eventos |
| GET | `/api/eventos/{id}` | Buscar por id |
| PUT | `/api/eventos/{id}` | Atualizar evento |
| PATCH | `/api/eventos/{id}/cancelamento` | Cancelar evento |
| GET | `/api/eventos/{id}/vagas` | Consultar vagas |

### Participantes
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/participantes` | Cadastrar participante |
| GET | `/api/participantes` | Listar participantes |
| GET | `/api/participantes/{id}` | Buscar por id |
| PUT | `/api/participantes/{id}` | Atualizar participante |
| GET | `/api/participantes/{participanteId}/inscricoes` | Listar inscrições do participante |

### Inscrições
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/eventos/{eventoId}/inscricoes` | Inscrever participante |
| GET | `/api/eventos/{eventoId}/inscricoes` | Listar inscrições do evento |
| GET | `/api/eventos/{eventoId}/inscricoes/{participanteId}` | Consultar inscrição |
| DELETE | `/api/eventos/{eventoId}/inscricoes/{participanteId}` | Cancelar inscrição |

## Corpo das Principais Requisições

### [POST] /api/eventos
```json
{
  "titulo": "Semana de Tecnologia",
  "descricao": "Evento acadêmico com palestras e oficinas.",
  "data": "2026-09-15",
  "horaInicio": "19:00",
  "horaFim": "22:30",
  "local": "Auditório Principal",
  "capacidadeMaxima": 120
  "modalidade": "RESTRICAO_IDADE",
  "idadeMinima": 18
}
```

### [PUT] /api/eventos/{id}
```json
{
  "titulo": "Semana de Tecnologia",
  "descricao": "Evento acadêmico com palestras e oficinas.",
  "data": "2026-09-15",
  "horaInicio": "19:00",
  "horaFim": "22:30",
  "local": "Auditório Principal",
  "capacidadeMaxima": 120
  "modalidade": "RESTRICAO_IDADE",
  "idadeMinima": 18
}
```
### [PATCH] /api/eventos/{id}/cancelamento
```json
{
  "motivoCancelamento": "Palestrante indisponível"
}
```

### [POST] /api/participantes
```json
{
  "nome": "Maria da Silva",
  "email": "maria.silva@example.com",
  "matricula": "2026001",
  "matriculaAtiva": true,
  "dataNascimento": "2000-05-10"
}
```

### [PUT] /api/participantes/{id}
```json
{
  "nome": "Maria da Silva",
  "email": "maria.silva@example.com"
}
```

### [POST] /api/eventos/{eventoId}/inscricoes
```json
{
  "participanteId": "550e8400-e29b-41d4-a716-446655440000"
}
```

## Exemplos de Respostas

### [POST] /api/eventos/{eventoId}/inscricoes — evento `ABERTO`

```json
{
  "id": 1,
  "eventoId": 1,
  "participanteId": "550e8400-e29b-41d4-a716-446655440000",
  "dataCriacao": "2026-10-05T09:30:00",
  "status": "CONFIRMADA",
  "motivoCancelamento": null,
  "comprovante": {
    "tipo": "SIMPLES",
    "inscricaoId": 1,
    "eventoId": 1,
    "participanteId": "550e8400-e29b-41d4-a716-446655440000",
    "dataInscricao": "2026-10-05T09:30:00",
    "resumo": "COMPROVANTE DE INSCRICAO\nInscricao: 1 (CONFIRMADA)\nParticipante: Maria da Silva\nEvento: Semana de Tecnologia\nData: 15/12/2026 as 19:00\nLocal: Auditório Principal"
  }
}
```

### [GET] /api/eventos/{id}/vagas
```json
{
  "id": 1,
  "capacidadeMaxima": 120,
  "inscricoesConfirmadas": 37,
  "vagasDisponiveis": 83
}
```

## Comprovantes

O comprovante é gerado automaticamente ao concluir a inscrição e já vem no campo `comprovante` da
resposta do `POST`.

- **Simples** (eventos `ABERTO`): resumo textual da inscrição, sem QR Code.
- **Digital completo** (eventos `EXCLUSIVO_ALUNOS` e `RESTRICAO_IDADE`): dados detalhados do evento e
  um QR Code. O `hash` do QR Code é um SHA-256 gerado a partir do identificador do evento, do identificador
  do participante e do momento da inscrição, e vai dentro do `payload`.
- Apenas inscrições **confirmadas** têm comprovante. Para uma inscrição cancelada, a consulta retorna `422`.


## Tratamento de Erros

Todas as respostas de erro seguem o mesmo formato:

```json
{
  "timestamp": "2026-10-05T19:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "mensagens": [
    "Inscrição permitida apenas para alunos com matrícula ativa."
  ],
  "path": "/api/eventos/2/inscricoes"
}
```
