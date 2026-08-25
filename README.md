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
}
```

### [POST] /api/participantes
```json
{
  "nome": "Maria da Silva",
  "email": "maria.silva@example.com"
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

